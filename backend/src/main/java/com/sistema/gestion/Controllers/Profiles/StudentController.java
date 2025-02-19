package com.sistema.gestion.Controllers.Profiles;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.DTO.PagedResponse;
import com.sistema.gestion.Models.Profiles.Student;
import com.sistema.gestion.Services.Profiles.StudentService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/estudiantes")
@Tag(name = "Student Controller", description = "Controlador para la gestión de estudiantes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StudentController {

	private final StudentService studentService;
	String user = "Pepe Hongo - admin";

	@GetMapping("/todos")
	public Mono<ResponseEntity<PagedResponse<Student>>> findAll(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String keyword) {
		return studentService.findAll(page, size, keyword)
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.noContent().build());
	}

	@GetMapping("/{id}")
	public Mono<Student> findById(@PathVariable String id) {
		return studentService.findById(id)
				.switchIfEmpty(Mono.error(new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Estudiante no encontrado con ID: " + id)))
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Error al buscar estudiante")));
	}

	@PostMapping("/crear")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Student> createStudent(@RequestBody @Valid Student student) {
		return studentService.createStudentWithCourses(student, user)
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.BAD_REQUEST, "Error al crear estudiante")));
	}

	@PutMapping("/actualizar/{id}")
	public Mono<Student> actualizarUsuario(@PathVariable String id, @RequestBody @Valid Student student) {
		return studentService.updateStudent(id, student, user)
				.switchIfEmpty(Mono.error(new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"No se pudo actualizar. Estudiante no encontrado con ID: " + id)))
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar estudiante")));
	}

	@DeleteMapping("/eliminar/{id}")
	public Mono<Void> eliminarUsuario(@PathVariable String id) {
		return studentService.deleteStudent(id)
				.switchIfEmpty(Mono.error(new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"No se pudo eliminar. Estudiante no encontrado con ID: " + id)))
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar estudiante.")));
	}
}