package com.sistema.gestion.Controllers.Devs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.Models.Devs.ErrorLog;
import com.sistema.gestion.Services.Devs.ErrorLogService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/errors")
@Tag(name = "Errors Controller", description = "Controlador para la gestión de errores")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ErrorLogController {
  private final ErrorLogService errorLogService;

  @GetMapping
  public Mono<ResponseEntity<Flux<ErrorLog>>> getAllErrorLogs() {
    return errorLogService.getAllErrorLogs()
        .collectList()
        .map(errorLog -> ResponseEntity.ok().body(Flux.fromIterable(errorLog)))
        .defaultIfEmpty(ResponseEntity.noContent().build());
  }

  @GetMapping("/{errorLogId}")
  public Mono<ResponseEntity<ErrorLog>> getErrorLogById(@PathVariable String errorLogId) {
    return errorLogService.getErrorLogById(errorLogId)
        .map(ResponseEntity::ok)
        .onErrorMap(e -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Error al tratar de obtener el Log con el ID: " + errorLogId))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }
}