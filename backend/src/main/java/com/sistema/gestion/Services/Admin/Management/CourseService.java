package com.sistema.gestion.Services.Admin.Management;

import static com.sistema.gestion.Utils.ErrorUtils.monoError;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.DAO.CourseDAO;
import com.sistema.gestion.DTO.CourseDTO;
import com.sistema.gestion.Models.Admin.Management.Course;
import com.sistema.gestion.Repositories.Admin.Management.CourseRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepo;
    private final CourseDAO courseDAO;

    public Flux<CourseDTO> findAllCourses(Integer page, Integer size) {
        return courseDAO.findAll(page, size);
    }

    public Mono<Long> findAllCount() {
        return courseRepo.count();
    }

    public Flux<CourseDTO> searchCourses(String keyword, Integer page, Integer size) {
        return courseDAO.searchCourses(keyword, page, size);
    }

    public Mono<CourseDTO> findCourseById(String id) {
        return courseDAO.findById(id).switchIfEmpty(
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
