package com.sistema.gestion.Repositories.Profiles;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.sistema.gestion.Models.Profiles.Student;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface StudentRepository extends ReactiveMongoRepository<Student, String> {
    @Query("{ $sort: { surname: 1 } }")
    Flux<Student> findBySurname(String surname);

    Mono<Student> findByDni(String dni);
}
