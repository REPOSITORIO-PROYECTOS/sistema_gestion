package com.sistema.gestion.DAO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.sistema.gestion.DTO.CourseDTO;
import com.sistema.gestion.DTO.IdAndNameDTO;
import com.sistema.gestion.Models.Admin.Management.Course;
import com.sistema.gestion.Repositories.Admin.Management.CourseRepository;
import com.sistema.gestion.Repositories.Profiles.StudentRepository;
import com.sistema.gestion.Repositories.Profiles.TeacherRepository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Getter
@Setter
@RequiredArgsConstructor
@Repository
public class CourseDAO {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public Flux<CourseDTO> findAll(Integer page, Integer size) {
    return courseRepository.findAll()
        .sort(Comparator.comparing(Course::getTitle))
        .skip((long) page * size)
        .take(size)
        .flatMap(course -> {
            List<String> studentIds = new ArrayList<>(course.getStudentsIds());

            Mono<Set<IdAndNameDTO>> studentsMono = studentRepository.findAllById(studentIds)
                .map(student -> new IdAndNameDTO(student.getId(), student.getSurname() + " " + student.getName()))
                .collectList()
                .map(HashSet::new);

            Mono<IdAndNameDTO> teacherMono = teacherRepository.findById(course.getTeacherId())
                .map(teacher -> new IdAndNameDTO(teacher.getId(), teacher.getSurname() + " " + teacher.getName()))
                .defaultIfEmpty(new IdAndNameDTO("", ""));

            return Mono.zip(studentsMono, teacherMono)
                .map(tuple -> new CourseDTO(
                    course.getId(), course.getTitle(), course.getDescription(),
                    course.getStatus(), course.getMonthlyPrice(), tuple.getT1(), tuple.getT2()
                ));
        });
    }

    public Flux<CourseDTO> searchCourses(String keyword, Integer page, Integer size) {
        return courseRepository.findByKeyword(keyword)
            .sort(Comparator.comparing(Course::getTitle))
            .skip((long) page * size)
            .take(size)
            .flatMap(course -> {
                List<String> studentIds = new ArrayList<>(course.getStudentsIds());
    
                Mono<Set<IdAndNameDTO>> studentsMono = studentRepository.findAllById(studentIds)
                    .map(student -> new IdAndNameDTO(student.getId(), student.getSurname() + " " + student.getName()))
                    .collectList()
                    .map(HashSet::new);
    
                Mono<IdAndNameDTO> teacherMono = teacherRepository.findById(course.getTeacherId())
                    .map(teacher -> new IdAndNameDTO(teacher.getId(), teacher.getSurname() + " " + teacher.getName()))
                    .defaultIfEmpty(new IdAndNameDTO("", ""));
    
                return Mono.zip(studentsMono, teacherMono)
                    .map(tuple -> new CourseDTO(
                        course.getId(), course.getTitle(), course.getDescription(),
                        course.getStatus(), course.getMonthlyPrice(), tuple.getT1(), tuple.getT2()
                    ));
            });
        }

}
