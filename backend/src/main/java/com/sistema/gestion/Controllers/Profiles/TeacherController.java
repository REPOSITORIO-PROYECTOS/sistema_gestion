package com.sistema.gestion.Controllers.Profiles;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/api/profesores")
@Tag(name = "Teacher Controller", description = "Controlador para la gestión de profesores")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TeacherController {

	private final TeacherService teacherService;

	@GetMapping("/todos")
	public Mono<ResponseEntity<PagedResponse<Teacher>>> findAll(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String keyword) {
		return teacherService.findAll(page, size, keyword)
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.noContent().build());
	}

	@GetMapping("/{id}")
	public Mono<ResponseEntity<Teacher>> findById(@PathVariable String id) {
		return teacherService.findById(id)
				.map(ResponseEntity::ok)
				.onErrorResume(ResponseStatusException.class,
						e -> Mono.just(ResponseEntity.status(e.getStatusCode()).body(null)))
				.onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(null)));
	}

	@PostMapping("/crear")
	public Mono<ResponseEntity<Teacher>> crear(@RequestBody @Valid Teacher teacher) {
		String user = "Pepe Hongo - admin";
		return teacherService.create(teacher, user)
				.map(savedTeacher -> ResponseEntity.status(HttpStatus.CREATED).body(savedTeacher))
				.onErrorMap(e -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Error al registrar el Profesor"));
	}

	@PutMapping("/actualizar/{id}")
	public Mono<ResponseEntity<Teacher>> actualizar(@PathVariable String id, @RequestBody @Valid Teacher teacher) {
		String user = "Pepe Hongo - admin";
		return teacherService.update(id, teacher, user)
				.map(ResponseEntity::ok)
				.onErrorResume(ResponseStatusException.class,
						e -> Mono.just(ResponseEntity.status(e.getStatusCode()).body(null)))
				.onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(null)));
	}

	@DeleteMapping("/eliminar/{id}")
	public Mono<Void> eliminarUsuario(@PathVariable String id) {
		return teacherService.delete(id)
				.switchIfEmpty(Mono.error(new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"No se pudo eliminar. Profesor no encontrado con ID: " + id)))
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar profesor.")));
	}

}
