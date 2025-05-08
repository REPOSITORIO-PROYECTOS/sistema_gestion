package com.sistema.gestion.Repositories.Admin.Management;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import org.springframework.stereotype.Repository;

import com.sistema.gestion.Models.Admin.Management.Course;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CourseRepository extends ReactiveMongoRepository<Course, String> {

    // Buscar cursos por keyword en el título o descripción con paginación
    @Query("{ $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } } ] }")
    Flux<Course> findByKeywordPaged(String keyword, PageRequest pageRequest);

    Flux<Course> findByTeacherIdsContaining(String professorId);

    Mono<Long> countByTeacherId(String professorId);

    // Contar la cantidad de cursos que coinciden con la keyword
    @Query(value = "{ $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } } ] }", count = true)
    Mono<Long> countByKeyword(String keyword);

    // Obtener todos los cursos con paginación
    @Query("{}")
    Flux<Course> findCoursesPaged(PageRequest pageRequest);
}
