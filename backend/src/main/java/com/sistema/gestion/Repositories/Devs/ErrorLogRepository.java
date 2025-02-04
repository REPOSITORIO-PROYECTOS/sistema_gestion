package com.sistema.gestion.Repositories.Devs;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.sistema.gestion.Models.Devs.ErrorLog;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ErrorLogRepository extends ReactiveMongoRepository<ErrorLog, String> {

  Flux<ErrorLog> findAllBy(PageRequest pageRequest);

  @Query(value = "{}", count = true)
  Mono<Long> countAll();

  Mono<Void> deleteAll();
}
