package com.sistema.gestion.Repositories.Profiles;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators.In;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.sistema.gestion.Models.Profiles.Student;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface StudentRepository extends ReactiveMongoRepository<Student, String> {

    // Busqueda con paginación
    @Query("{}")
    Flux<Student> findAllBy(PageRequest pageRequest);

    Mono<Student> findByEmail(String email);

    Mono<Student> findByDni(String dni);

    // Conteo total de registros
    @Query(value = "{}", count = true)
    Mono<Long> countAll();

    // Busqueda por DNI o Apellido con paginación
    @Query("{ '$or': [ { 'dni': { $regex: ?0, $options: 'i' } }, { 'surname': { $regex: ?0, $options: 'i' } } ] }")
    Flux<Student> findByDniOrSurname(String query, PageRequest pageRequest);

    // Conteo de registros por DNI o Apellido
    @Query(value = "{ '$or': [ { 'dni': { $regex: ?0, $options: 'i' } }, { 'surname': { $regex: ?0, $options: 'i' } } ] }", count = true)
    Mono<Long> countByDniOrSurname(String query);

    Mono<Long> countByCursesIds(String courseId);

    Flux<Student> findByCursesIds(String courseId, PageRequest pageRequest);

    // Busqueda de estudiantes por apellido con paginación
    @Query("{ 'surname' : ?0 }")
    Flux<Student> findBySurname(String surname, PageRequest pageRequest);

    // Busqueda de estudiantes por ID de curso
    @Query("{ 'coursesIds': ?0 }")
    Flux<Student> findAllByCurseId(String courseId);
}
