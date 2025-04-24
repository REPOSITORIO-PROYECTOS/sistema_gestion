package com.sistema.gestion.Controllers.Profiles;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.DTO.PagedResponse;
import com.sistema.gestion.Models.Profiles.Parent;
import com.sistema.gestion.Models.Profiles.Student;
import com.sistema.gestion.Services.Profiles.ParentService;
import com.sistema.gestion.Services.Profiles.StudentService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;



@RestController
@RequestMapping("/api/padres")
@Tag(name = "Parents Controller", description = "Controlador para la gestión de padres")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ParentsController {

	private final ParentService parentService;

	@Operation(summary = "Obtener todos los padres", description = "Obtiene una lista paginada de padres, opcionalmente filtrada por palabra clave")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Padres obtenidos exitosamente"),
			@ApiResponse(responseCode = "204", description = "No hay padres para mostrar"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@GetMapping("/paged")
	public Mono<ResponseEntity<PagedResponse<Parent>>> findAll(
			@Parameter(description = "Número de página", example = "0") @RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Tamaño de la página", example = "10") @RequestParam(defaultValue = "10") int size,
			@Parameter(description = "Palabra clave para filtrar", example = "Juan") @RequestParam(required = false) String keyword) {
		return parentService.findAll(page, size, keyword)
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.noContent().build());
	}

	@Operation(summary = "Obtener padre por ID", description = "Obtiene un padre por su ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Padre obtenido exitosamente"),
			@ApiResponse(responseCode = "404", description = "Padre no encontrado"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@GetMapping("/{id}")
	public Mono<Parent> findById(
			@Parameter(description = "ID del padre", required = true) @PathVariable String id) {
		return parentService.findById(id)
				.switchIfEmpty(Mono.error(new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Padre no encontrado con ID: " + id)))
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Error al buscar padre")));
	}

	@Operation(summary = "Crear padre", description = "Crea un nuevo padre")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Padre creado exitosamente"),
			@ApiResponse(responseCode = "400", description = "Datos del padre inválidos"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@PostMapping("/crear")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Parent> createStudent(
			Authentication auth,
			@Parameter(description = "Datos del padre", required = true) @RequestBody @Valid Parent parent) {

		String user = auth.getName();

		return parentService.createParent(parent, user)
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.BAD_REQUEST, "Error al crear padre")));
	}

	@Operation(summary = "Actualizar padre", description = "Actualiza un padre existente")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Padre actualizado exitosamente"),
			@ApiResponse(responseCode = "404", description = "Padre no encontrado"),
			@ApiResponse(responseCode = "400", description = "Datos del padre inválidos"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@PutMapping("/actualizar/{id}")
	public Mono<Parent> actualizarUsuario(
			Authentication auth,
			@Parameter(description = "ID del padre", required = true) @PathVariable String id,
			@Parameter(description = "Datos actualizados del padre", required = true) @RequestBody @Valid Parent parent) {

		String user = auth.getName();

		return parentService.updateParent(id, parent, user)
				.switchIfEmpty(Mono.error(new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"No se pudo actualizar. Padre no encontrado con ID: " + id)))
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar padre")));
	}

	@Operation(summary = "Eliminar padre", description = "Elimina un padre por su ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Padre eliminado exitosamente"),
			@ApiResponse(responseCode = "404", description = "Padre no encontrado"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@DeleteMapping("/eliminar/{id}")
	public Mono<Void> eliminarUsuario(
			@Parameter(description = "ID del padre", required = true) @PathVariable String id) {
		return parentService.deleteParent(id)
				.switchIfEmpty(Mono.error(new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"No se pudo eliminar. Padre no encontrado con ID: " + id)))
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar padre.")));
	}
}
