package com.sistema.gestion.Services.Admin.Management;

import static com.sistema.gestion.Utils.ErrorUtils.monoError;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sistema.gestion.Models.Admin.Management.Course;
import com.sistema.gestion.Repositories.Admin.Management.CourseRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CourseService {

    @Autowired
    private final CourseRepository courseRepo;

    public CourseService(CourseRepository courseRepo) {
        this.courseRepo = courseRepo;
    }

    public Flux<Course> findAllCourses() {
        return courseRepo.findAll();
    }

    public Mono<Course> findCourseById(String id) {
        return courseRepo.findById(id)
                .switchIfEmpty(monoError(HttpStatus.NOT_FOUND, "No hay curso con el ID: " + id));
    }

    public Flux<Course> searchCourses(String keyword) {
        return courseRepo.findByKeyword(keyword);
    }

    public Mono<Course> saveCourse(Course course, String user) {

        if (course.getId() == null || course.getId().equals("")) {
            course.setCreatedAt(LocalDateTime.now());
            course.setCreatedBy(user);
            return courseRepo.save(course);
        }

        return courseRepo.findById(course.getId())
                .flatMap(existingCourse -> {
                    existingCourse.setUpdatedAt(LocalDateTime.now());
                    existingCourse.setModifiedBy(user);
                    existingCourse.setTitle(course.getTitle());
                    existingCourse.setDescription(course.getDescription());
                    existingCourse.setStatus(course.getStatus());
                    existingCourse.setMonthlyPrice(course.getMonthlyPrice());
                    existingCourse.setTeacherId(course.getTeacherId());
                    return courseRepo.save(existingCourse);
                });
    }

    public Mono<Void> deleteCourse(String courseId) {
        return courseRepo.findById(courseId)
                .switchIfEmpty(monoError(HttpStatus.NOT_FOUND, "No hay curso con el ID: " + courseId))
                .flatMap(courseRepo::delete);
    }

}
