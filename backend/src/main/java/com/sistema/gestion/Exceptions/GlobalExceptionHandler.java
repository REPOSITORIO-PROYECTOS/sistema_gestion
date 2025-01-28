package com.sistema.gestion.Exceptions;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.web.server.ServerWebExchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.Models.Devs.ErrorLog;
import com.sistema.gestion.Repositories.Devs.ErrorLogRepository;

import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {

  @Autowired
  private ErrorLogRepository errorLogRepository;

  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<String>> handleGeneralException(Exception ex, ServerWebExchange exchange) {
    return saveErrorLog(ex, exchange)
        .then(Mono.just(ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Se ha producido un error inesperado.")));
  }

  @ExceptionHandler(ResponseStatusException.class)
  public Mono<ResponseEntity<String>> handleResponseStatusException(ResponseStatusException ex,
      ServerWebExchange exchange) {
    return saveErrorLog(ex, exchange)
        .then(Mono.just(ResponseEntity
            .status(ex.getStatusCode())
            .body(ex.getReason())));
  }

  private Mono<ErrorLog> saveErrorLog(Exception ex, ServerWebExchange exchange) {
    ErrorLog log = new ErrorLog();
    log.setMessage(ex.getMessage());
    log.setExceptionType(ex.getClass().getName());
    log.setStackTrace(Arrays.toString(ex.getStackTrace()));
    log.setTimestamp(LocalDateTime.now());
    log.setPath(exchange.getRequest().getPath().toString());
    log.setMethod(exchange.getRequest().getMethod().toString());
    log.setHeaders(exchange.getRequest().getHeaders().toSingleValueMap());
    log.setQueryParams(exchange.getRequest().getQueryParams().toSingleValueMap());
    log.setCustomMessage("");
    return errorLogRepository.save(log);
  }
}
