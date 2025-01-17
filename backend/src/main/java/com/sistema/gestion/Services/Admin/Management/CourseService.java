package com.sistema.gestion.Services.Admin.Management;

import static com.sistema.gestion.Utils.ErrorUtils.monoError;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.Models.Admin.Management.Course;
import com.sistema.gestion.Repositories.Admin.Management.CourseRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepo;

    public Flux<Course> findAllCourses(Integer page, Integer size) {
        return courseRepo.findAll()
                .sort((course1, course2) -> course1.getTitle().compareTo(course2.getTitle()))
                .skip((long) page * size)
                .take(size);
    }

    public Flux<Course> searchCourses(String keyword, Integer page, Integer size) {
        return courseRepo.findByKeyword(keyword)
                .sort((course1, course2) -> course1.getTitle().compareTo(course2.getTitle()))
                .skip((long) page * size)                
                .take(size);
    }

    public Mono<Course> findCourseById(String id) {
        return courseRepo.findById(id)
                .switchIfEmpty(
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay curso con el ID: " + id)));
    }

    public Mono<Course> saveCourse(Course course, String user) {

        if (course.getId() != null && !course.getId().isEmpty()) {
            return monoError(HttpStatus.BAD_REQUEST, "El curso ya tiene un ID resgistrado,"
                    + " no se puede almacenar un nuevo curso con ID ya registrado");
        }
        course.setCreatedAt(LocalDateTime.now());
        course.setCreatedBy(user);
        return courseRepo.save(course);

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
                });

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
