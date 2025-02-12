package com.sistema.gestion.Services.Profiles;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.DTO.PagedResponse;
import com.sistema.gestion.Models.Profiles.Student;
import com.sistema.gestion.Repositories.Admin.Management.CourseRepository;
import com.sistema.gestion.Repositories.Profiles.StudentRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class StudentService {

  private final StudentRepository studentRepository;
  private final CourseRepository courseRepository;

  public Mono<PagedResponse<Student>> findAll(int page, int size, String keyword) {
    PageRequest pageRequest = PageRequest.of(page, size);

    if (keyword != null && !keyword.isEmpty()) {
      Mono<Long> totalElementsMono = studentRepository.countByDniOrSurname(keyword);
      Flux<Student> studentFlux = studentRepository.findByDniOrSurname(keyword, pageRequest);

      return Mono.zip(totalElementsMono, studentFlux.collectList())
          .map(tuple -> new PagedResponse<>(
              tuple.getT2(), // Lista de cursos filtrados
              tuple.getT1(), // Total de registros filtrados
              page,
              size));
    }

    Mono<Long> totalElementsMono = studentRepository.countAll();
    Flux<Student> studentsFlux = studentRepository.findAllBy(pageRequest);

    return Mono.zip(totalElementsMono, studentsFlux.collectList())
        .map(tuple -> new PagedResponse<>(
            tuple.getT2(), // Lista de estudiantes
            tuple.getT1(), // Total de registros
            page,
            size));
  }

  public Mono<PagedResponse<Student>> searchStudents(String query, int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size);

    // Contar total de coincidencias en DNI o Apellido
    Mono<Long> totalElementsMono = studentRepository.countByDniOrSurname(query);
    // Obtener lista de coincidencias con paginación
    Flux<Student> studentsFlux = studentRepository.findByDniOrSurname(query, pageRequest);

    return Mono.zip(totalElementsMono, studentsFlux.collectList())
        .map(tuple -> new PagedResponse<>(
            tuple.getT2(), // Lista de estudiantes encontrados
            tuple.getT1(), // Total de registros encontrados
            page,
            size));
  }

  public Mono<Long> findAllCount() {
    return studentRepository.count();
  }

  public Mono<Student> createStudentWithCourses(Student student, String user) {
    student.setCreatedBy(user);

    return studentRepository.save(student)
        .flatMap(savedStudent -> {
          Set<String> courseIds = savedStudent.getCoursesIds();

          if (courseIds == null || courseIds.isEmpty()) {
            return Mono.just(savedStudent);
          }

          return Flux.fromIterable(courseIds)
              .flatMap(courseId -> courseRepository.findById(courseId)
                  .switchIfEmpty(
                      Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado: " + courseId)))
                  .flatMap(course -> {
                    course.getStudentsIds().add(savedStudent.getId()); // Agregamos el estudiante al curso
                    return courseRepository.save(course); // Guardamos el curso actualizado
                  }))
              .then(Mono.just(savedStudent)); // Devolvemos el estudiante guardado
        })
        .onErrorMap(e -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
            "Error al inscribir al estudiante en los cursos", e));
  }

  public Mono<Student> findById(String id) {
    return studentRepository.findById(id)
        .switchIfEmpty(
            Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el estudiante con ID: " + id)))
        .onErrorMap(
            e -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al buscar el estudiante", e));
  }

  public Mono<Student> updateStudent(String id, Student student, String user) {
    return studentRepository.findById(id)
        .switchIfEmpty(
            Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el estudiante con ID: " + id)))
        .flatMap(existingStudent -> {
          existingStudent.setName(student.getName());
          existingStudent.setSurname(student.getSurname());
          existingStudent.setUpdatedAt(LocalDateTime.now());
          existingStudent.setModifiedBy(user);
          existingStudent.setEmail(student.getEmail());
          existingStudent.setPhone(student.getPhone());
          return studentRepository.save(existingStudent)
              .onErrorMap(e -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                  "Error al actualizar el estudiante", e));
        });
  }

  public Mono<Void> deleteStudent(String id) {
    return studentRepository.findById(id)
        .switchIfEmpty(
            Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el estudiante con ID: " + id)))
        .flatMap(student -> studentRepository.deleteById(id)
            .onErrorMap(e -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error al eliminar el estudiante", e)));
  }
}
