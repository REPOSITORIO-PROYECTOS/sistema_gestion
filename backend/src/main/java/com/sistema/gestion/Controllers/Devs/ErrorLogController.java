package com.sistema.gestion.Controllers.Devs;

import com.sistema.gestion.DTO.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
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
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/errors")
@Tag(name = "Errors Controller", description = "Controlador para la gestión de errores")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ErrorLogController {
	private final ErrorLogService errorLogService;

	@Operation(summary = "Obtener registros de errores paginados",
			description = "Retorna una lista paginada de registros de errores. Se puede filtrar por palabra clave.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Lista de errores obtenida correctamente"),
			@ApiResponse(responseCode = "400", description = "Solicitud incorrecta")
	})
	@GetMapping("/paged")
	public Mono<ResponseEntity<PagedResponse<ErrorLog>>> getAllErrorLogs(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
		return errorLogService.getErrorLogsPaged(page, size, keyword, startDate, endDate)
				.map(ResponseEntity::ok)
				.onErrorMap(e -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener registros paginados"));
	}

	@Operation(summary = "Obtener un registro de error por ID",
			description = "Retorna un registro de error específico basado en su ID.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Registro de error encontrado"),
			@ApiResponse(responseCode = "404", description = "Registro de error no encontrado"),
			@ApiResponse(responseCode = "400", description = "Solicitud incorrecta")
	})
	@GetMapping("/{errorLogId}")
	public Mono<ResponseEntity<ErrorLog>> getErrorLogById(@PathVariable String errorLogId) {
		return errorLogService.getErrorLogById(errorLogId)
				.map(ResponseEntity::ok)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro de error no encontrado")))
				.onErrorMap(e -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener el registro con ID: " + errorLogId));
	}

	@DeleteMapping
	@Operation(summary = "Eliminar todos los registros de error", description = "Elimina todos los registros de error del sistema.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Registros eliminados exitosamente"),
			@ApiResponse(responseCode = "500", description = "Error interno al eliminar los registros de error")
	})
	public Mono<ResponseEntity<Void>> deleteAllErrorLogs() {
		return errorLogService.deleteAllErrorLogs()
				.then(Mono.just(ResponseEntity.noContent().build()));
	}


}
