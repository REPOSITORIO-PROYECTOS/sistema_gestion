package com.sistema.gestion.Services.Devs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistema.gestion.Models.Devs.ErrorLog;
import com.sistema.gestion.Repositories.Devs.ErrorLogRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ErrorLogService {
  private final ErrorLogRepository errorLogRepo;

  public Flux<ErrorLog> getAllErrorLogs() {
    return errorLogRepo.findAll();
  }

  public Mono<ErrorLog> getErrorLogById(String id) {
    return errorLogRepo.findById(id);
  }
}