package com.sistema.gestion.Repositories.Devs;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.sistema.gestion.Models.Devs.ErrorLog;

public interface ErrorLogRepository extends ReactiveMongoRepository<ErrorLog, String> {
}