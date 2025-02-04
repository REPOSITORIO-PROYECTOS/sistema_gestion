package com.sistema.gestion.Services.Devs;

import org.springframework.data.domain.PageRequest;
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

  public Flux<ErrorLog> getAllErrorLogs(PageRequest pageRequest) {
    return errorLogRepo.findAllBy(pageRequest);
  }

  public Mono<Long> countAllErrorLogs() {
    return errorLogRepo.countAll();
  }

  public Mono<ErrorLog> getErrorLogById(String id) {
    return errorLogRepo.findById(id);
  }

  public Mono<Void> deleteAllErrorLogs() {
    return errorLogRepo.deleteAll();
  }
}
