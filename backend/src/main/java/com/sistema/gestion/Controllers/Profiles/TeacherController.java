package com.sistema.gestion.Controllers.Profiles;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.DTO.PagedResponse;
import com.sistema.gestion.Models.Profiles.Teacher;
import com.sistema.gestion.Services.Profiles.TeacherService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/profesores")
@Tag(name = "Teacher Controller", description = "Controlador para la gestión de profesores")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TeacherController {

	private final TeacherService teacherService;

	@Operation(summary = "Obtener todos los profesores", description = "Obtiene una lista paginada de profesores, opcionalmente filtrada por palabra clave")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Profesores obtenidos exitosamente"),
			@ApiResponse(responseCode = "204", description = "No hay profesores para mostrar"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@GetMapping("/paged")
	public Mono<ResponseEntity<PagedResponse<Teacher>>> findAll(
			@Parameter(description = "Número de página", example = "0") @RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Tamaño de la página", example = "10") @RequestParam(defaultValue = "10") int size,
			@Parameter(description = "Palabra clave para filtrar", example = "Juan") @RequestParam(required = false) String keyword) {
		return teacherService.findAll(page, size, keyword)
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.noContent().build());
	}

	@Operation(summary = "Obtener profesor por ID", description = "Obtiene un profesor por su ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Profesor obtenido exitosamente"),
			@ApiResponse(responseCode = "404", description = "Profesor no encontrado"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Teacher>> findById(
			@Parameter(description = "ID del profesor", required = true) @PathVariable String id) {
		return teacherService.findById(id)
				.map(ResponseEntity::ok)
				.onErrorResume(ResponseStatusException.class,
						e -> Mono.just(ResponseEntity.status(e.getStatusCode()).body(null)))
				.onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(null)));
	}

	@Operation(summary = "Crear profesor", description = "Crea un nuevo profesor")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Profesor creado exitosamente"),
			@ApiResponse(responseCode = "400", description = "Datos del profesor inválidos"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@PostMapping("/crear")
	public Mono<ResponseEntity<Teacher>> crear(
			Authentication auth,
			@Parameter(description = "Datos del profesor", required = true) @RequestBody @Valid Teacher teacher) {

		String user = auth.getName();

		return teacherService.create(teacher, user)
				.map(savedTeacher -> ResponseEntity.status(HttpStatus.CREATED).body(savedTeacher))
				.onErrorMap(e -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Error al registrar el Profesor"));
	}

	@Operation(summary = "Actualizar profesor", description = "Actualiza un profesor existente")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Profesor actualizado exitosamente"),
			@ApiResponse(responseCode = "404", description = "Profesor no encontrado"),
			@ApiResponse(responseCode = "400", description = "Datos del profesor inválidos"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@PutMapping("/actualizar/{id}")
	public Mono<ResponseEntity<Teacher>> actualizar(
			Authentication auth,
			@Parameter(description = "ID del profesor", required = true) @PathVariable String id,
			@Parameter(description = "Datos actualizados del profesor", required = true) @RequestBody @Valid Teacher teacher) {

		String user = auth.getName();

		return teacherService.update(id, teacher, user)
				.map(ResponseEntity::ok)
				.onErrorResume(ResponseStatusException.class,
						e -> Mono.just(ResponseEntity.status(e.getStatusCode()).body(null)))
				.onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(null)));
	}

	@Operation(summary = "Eliminar profesor", description = "Elimina un profesor por su ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Profesor eliminado exitosamente"),
			@ApiResponse(responseCode = "404", description = "Profesor no encontrado"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@DeleteMapping("/eliminar/{id}")
	public Mono<Void> eliminarUsuario(
			@Parameter(description = "ID del profesor", required = true) @PathVariable String id) {
		return teacherService.delete(id)
				.switchIfEmpty(Mono.error(new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"No se pudo eliminar. Profesor no encontrado con ID: " + id)))
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar profesor.")));
	}
}