package com.sistema.gestion.Services.Admin.Management;

import static com.sistema.gestion.Utils.ErrorUtils.monoError;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.DTO.PagedResponse;
import com.sistema.gestion.Models.Admin.Management.Course;
import com.sistema.gestion.Repositories.Admin.Management.CourseRepository;
import com.sistema.gestion.Repositories.Profiles.StudentRepository;
import com.sistema.gestion.Repositories.Profiles.TeacherRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CourseService {

  private final CourseRepository courseRepo;
  private final StudentRepository studentRepo;
  private final TeacherRepository teacherRepo;

  public Mono<PagedResponse<Course>> getCoursesPaged(int page, int size, String keyword) {
    PageRequest pageRequest = PageRequest.of(page, size);

    if (keyword != null && !keyword.isEmpty()) {
      Mono<Long> totalElementsMono = courseRepo.countByKeyword(keyword);
      Flux<Course> coursesFlux = courseRepo.findByKeywordPaged(keyword, pageRequest);

      return Mono.zip(totalElementsMono, coursesFlux.collectList())
          .map(tuple -> new PagedResponse<>(
              tuple.getT2(), // Lista de cursos filtrados
              tuple.getT1(), // Total de registros filtrados
              page,
              size));
    }

    // Si no hay keyword, obtener todos los cursos paginados
    Mono<Long> totalElementsMono = courseRepo.count();
    Flux<Course> coursesFlux = courseRepo.findCoursesPaged(pageRequest);

    return Mono.zip(totalElementsMono, coursesFlux.collectList())
        .map(tuple -> new PagedResponse<>(
            tuple.getT2(), // Lista de cursos
            tuple.getT1(), // Total de registros
            page,
            size));
  }

  public Mono<Course> saveCourse(Course course, String user) {
    if (course.getId() != null && !course.getId().isEmpty()) {
      return monoError(HttpStatus.BAD_REQUEST,
          "El curso ya tiene un ID registrado, no se puede almacenar un nuevo curso con ID ya registrado");
    }

    course.setCreatedAt(LocalDateTime.now());
    course.setCreatedBy(user);

    return courseRepo.save(course)
        .flatMap(savedCourse -> {

          if (savedCourse.getTeacherId() == null || savedCourse.getTeacherId().isEmpty()) {
            return Mono.just(savedCourse); // Si no hay profesor devolvemos el curso sin cambios
          }

          return teacherRepo.findById(savedCourse.getTeacherId())
              .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                  "Profesor no encontrado: " + savedCourse.getTeacherId())))
              .flatMap(teacher -> {
                if (teacher.getCoursesIds() == null || teacher.getCoursesIds().isEmpty()) {
                  Set<String> courses = new HashSet<>();
                  courses.add(savedCourse.getId());
                  teacher.setCoursesIds(courses);
                  return teacherRepo.save(teacher);
                }
                Set<String> updatedCourses = new HashSet<>(teacher.getCoursesIds());
                updatedCourses.add(savedCourse.getId());
                teacher.setCoursesIds(updatedCourses);
                return teacherRepo.save(teacher);
              })
              .thenReturn(savedCourse); // Retornamos el curso guardado
        });
  }

  public Mono<Course> updateCourse(Course course, String courseId, String user) {
    if (!course.getId().equals(courseId)) {
      return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Los IDs del curso a actualizar " +
              "en la base de datos con el del cuerpo de la solicitud no coinciden."));
    }
    return courseRepo.findById(courseId)
        .flatMap(existingCourse -> {
          return courseRepo.save(mappingCourseToUpdate(existingCourse, course, user));
        })
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
            "No se encontró el curso con ID: " + courseId)));

  }

  public Mono<Course> registerStudentInCourse(String courseId, String studentId) {
    if (studentId == null) {
      return monoError(HttpStatus.NOT_FOUND, "No hay estudiante con el ID: " + studentId);
    }

    return courseRepo.findById(courseId)
        .switchIfEmpty(monoError(HttpStatus.NOT_FOUND, "No hay estudiante con el ID: " + studentId))
        .flatMap(existingCourse -> {
          Set<String> enrolledStudents = existingCourse.getStudentsIds();
          if (enrolledStudents == null) {
            enrolledStudents = new HashSet<>();
            existingCourse.setStudentsIds(enrolledStudents);
          }
          enrolledStudents.add(studentId);
          return studentRepo.findById(studentId)
              .switchIfEmpty(monoError(HttpStatus.NOT_FOUND, "No hay estudiante con el ID: " + studentId))
              .flatMap(student -> {
                Set<String> studentCourses = student.getCoursesIds();
                if (studentCourses == null) {
                  studentCourses = new HashSet<>();
                  student.setCoursesIds(studentCourses);
                }
                studentCourses.add(courseId);
                return studentRepo.save(student)
                    .flatMap(savedStudent -> courseRepo.save(existingCourse));
              });
        });
  }

  public Mono<Course> removeStudentFromCourse(String courseId, String studentId) {
    return courseRepo.findById(courseId)
        .flatMap(existingCourse -> {
          Set<String> enrolledStudents = existingCourse.getStudentsIds();
          if (enrolledStudents != null && enrolledStudents.contains(studentId)) {
            enrolledStudents.remove(studentId);
            return studentRepo.findById(studentId)
                .flatMap(student -> {
                  Set<String> studentCourses = student.getCoursesIds();
                  if (studentCourses != null && studentCourses.contains(courseId)) {
                    studentCourses.remove(courseId);
                    return studentRepo.save(student);
                  }
                  return Mono.empty();
                })
                .then(courseRepo.save(existingCourse));
          }
          return monoError(HttpStatus.NOT_FOUND, "Alumno no inscripto en el curso");
        })
        .switchIfEmpty(monoError(HttpStatus.NOT_FOUND, "No hay curso con el ID: " + courseId));
  }

  public Mono<Void> deleteCourse(String courseId) {
    return courseRepo.findById(courseId)
        .switchIfEmpty(monoError(HttpStatus.NOT_FOUND, "No hay curso con el ID: " + courseId))
        .flatMap(courseRepo::delete);
  }

  /** Metodos locales */
  private Course mappingCourseToUpdate(Course existingCourse, Course course, String user) {
    existingCourse.setUpdatedAt(LocalDateTime.now());
    existingCourse.setModifiedBy(user);
    existingCourse.setTitle(course.getTitle());
    existingCourse.setDescription(course.getDescription());
    existingCourse.setStatus(course.getStatus());
    existingCourse.setMonthlyPrice(course.getMonthlyPrice());
    existingCourse.setTeacherId(course.getTeacherId());
    return existingCourse;
  }
}