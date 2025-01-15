package com.sistema.gestion.Repositories.Profiles;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.sistema.gestion.Models.Profiles.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {
    @Query("{ $sort: { surname: 1 } }")
    Flux<User> findBySurname(String surname);
    
    Mono<User> findByDni(String dni);
}

