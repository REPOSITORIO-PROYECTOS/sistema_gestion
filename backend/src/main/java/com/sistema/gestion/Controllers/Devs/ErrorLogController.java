package com.sistema.gestion.Controllers.Devs;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	public Mono<ResponseEntity<Flux<ErrorLog>>> getAllErrorLogs(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		PageRequest pageRequest = PageRequest.of(page, size);
		return errorLogService.countAllErrorLogs()
				.flatMap(count -> {
					if (count == 0) {
						return Mono.just(ResponseEntity.noContent().build());
					}
					return Mono.just(ResponseEntity.ok().body(errorLogService.getAllErrorLogs(pageRequest)));
				});
	}

	@GetMapping("/{errorLogId}")
	public Mono<ResponseEntity<ErrorLog>> getErrorLogById(@PathVariable String errorLogId) {
		return errorLogService.getErrorLogById(errorLogId)
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.notFound().build())
				.onErrorMap(e -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Error al tratar de obtener el Log con el ID: " + errorLogId));
	}

	@DeleteMapping
	public Mono<ResponseEntity<Void>> deleteAllErrorLogs() {
		return errorLogService.deleteAllErrorLogs()
				.then(Mono.just(ResponseEntity.noContent().build()));
	}
}