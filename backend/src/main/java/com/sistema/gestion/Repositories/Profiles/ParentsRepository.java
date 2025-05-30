package com.sistema.gestion.Repositories.Profiles;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.sistema.gestion.Models.Profiles.Parent;
import com.sistema.gestion.Models.Profiles.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ParentsRepository extends ReactiveMongoRepository<Parent, String> {

    // Busqueda con paginación
    @Query("{}")
    Flux<Parent> findAllBy(PageRequest pageRequest);

    Mono<Parent> findByEmail(String email);

    Mono<Parent> findByDni(String dni);

    // Conteo total de registros
    @Query(value = "{}", count = true)
    Mono<Long> countAll();

    // Busqueda por DNI o Apellido con paginación
    @Query("{ '$or': [ { 'dni': ?0 }, { 'surname': ?0 } ] }")
    Flux<Parent> findByDniOrSurname(String query, PageRequest pageRequest);

    // Conteo de registros por DNI o Apellido
    @Query(value = "{ '$or': [ { 'dni': ?0 }, { 'surname': ?0 } ] }", count = true)
    Mono<Long> countByDniOrSurname(String query);

    // Buscar usuarios por keyword en el título o descripción con paginación
    @Query("{ $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'surname': { $regex: ?0, $options: 'i' } } ] }")
    Flux<Parent> findByKeywordPaged(String keyword, PageRequest pageRequest);

    // Contar la cantidad de usuarios que coinciden con la keyword
    @Query(value = "{ $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'surname': { $regex: ?0, $options: 'i' } } ] }", count = true)
    Mono<Long> countByKeyword(String keyword);

    // Busqueda de estudiantes por apellido con paginación
    @Query("{ 'surname' : ?0 }")
    Flux<Parent> findBySurname(String surname, PageRequest pageRequest);
}
