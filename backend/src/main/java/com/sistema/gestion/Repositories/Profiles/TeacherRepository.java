package com.sistema.gestion.Repositories.Profiles;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.sistema.gestion.Models.Profiles.Teacher;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TeacherRepository extends ReactiveMongoRepository<Teacher, String> {
    // Busqueda con paginación
    @Query("{}")
    Flux<Teacher> findAllBy(PageRequest pageRequest);

    Mono<Teacher> findByEmail(String email);

    Mono<Teacher> findByDni(String dni);

    // Conteo total de registros
    @Query(value = "{}", count = true)
    Mono<Long> countAll();

    // Busqueda por DNI o Apellido con paginación
    @Query("{ '$or': [ { 'dni': ?0 }, { 'surname': ?0 } ] }")
    Flux<Teacher> findByDniOrSurname(String query, PageRequest pageRequest);

    // Conteo de registros por DNI o Apellido
    @Query(value = "{ '$or': [ { 'dni': ?0 }, { 'surname': ?0 } ] }", count = true)
    Mono<Long> countByDniOrSurname(String query);
}
