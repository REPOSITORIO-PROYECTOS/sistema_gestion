package com.sistema.gestion.Controllers.Profiles;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.DTO.PagedResponse;
import com.sistema.gestion.Models.Profiles.Student;
import com.sistema.gestion.Services.Profiles.StudentService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("/api/estudiantes")
@Tag(name = "Student Controller", description = "Controlador para la gestión de estudiantes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StudentController {

	private final StudentService studentService;

	@Operation(summary = "Obtener todos los estudiantes", description = "Obtiene una lista paginada de estudiantes, opcionalmente filtrada por palabra clave")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Estudiantes obtenidos exitosamente"),
			@ApiResponse(responseCode = "204", description = "No hay estudiantes para mostrar"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@GetMapping("/paged")
	public Mono<ResponseEntity<PagedResponse<Student>>> findAll(
			@Parameter(description = "Número de página", example = "0") @RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Tamaño de la página", example = "10") @RequestParam(defaultValue = "10") int size,
			@Parameter(description = "Palabra clave para filtrar", example = "Juan") @RequestParam(required = false) String keyword) {
		return studentService.findAll(page, size, keyword)
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.noContent().build());
	}

	@GetMapping("/getStudentsByCourseId")
	public Mono<ResponseEntity<PagedResponse<Student>>> getStudentsByCourseId(
			@Parameter(description = "ID del curso", required = true) @RequestParam String courseId,
			@Parameter(description = "Número de página", example = "0") @RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Tamaño de la página", example = "10") @RequestParam(defaultValue = "10") int size) {
		return studentService.getStudentsByCourseId(courseId, page, size)
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.noContent().build());
	}
	public String getMethodName(@RequestParam String param) {
		return new String();
	}
	

	@Operation(summary = "Obtener estudiante por ID", description = "Obtiene un estudiante por su ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Estudiante obtenido exitosamente"),
			@ApiResponse(responseCode = "404", description = "Estudiante no encontrado"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@GetMapping("/obtener/{id}")
	public Mono<Student> findById(
			@Parameter(description = "ID del estudiante", required = true) @PathVariable String id) {
		return studentService.findById(id)
				.switchIfEmpty(Mono.error(new ResponseStatusException(
						HttpStatus.NOT_FOUND, "Estudiante no encontrado con ID: " + id)))
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Error al buscar estudiante")));
	}

	@Operation(summary = "Crear estudiante", description = "Crea un nuevo estudiante")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Estudiante creado exitosamente"),
			@ApiResponse(responseCode = "400", description = "Datos del estudiante inválidos"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@PostMapping("/crear")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Student> createStudent(
			Authentication auth,
			@Parameter(description = "Datos del estudiante", required = true) @RequestBody @Valid Student student) {

		String user = auth.getName();

		return studentService.createStudentWithCourses(student, user)
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.BAD_REQUEST, "Error al crear estudiante")));
	}

	@PostMapping("/crearConPadres")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Student> createStudentWithParents(
			Authentication auth,
			@Parameter(description = "Datos del estudiante", required = true) @RequestBody @Valid Student student) {

		String user = auth.getName();

		return studentService.createStudentWithCoursesAndParents(student, user)
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.BAD_REQUEST, "Error al crear estudiante")));
	}

	@Operation(summary = "Actualizar estudiante", description = "Actualiza un estudiante existente")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Estudiante actualizado exitosamente"),
			@ApiResponse(responseCode = "404", description = "Estudiante no encontrado"),
			@ApiResponse(responseCode = "400", description = "Datos del estudiante inválidos"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@PutMapping("/actualizar/{id}")
	public Mono<Student> actualizarUsuario(
			Authentication auth,
			@Parameter(description = "ID del estudiante", required = true) @PathVariable String id,
			@Parameter(description = "Datos actualizados del estudiante", required = true) @RequestBody @Valid Student student) {

		String user = auth.getName();

		return studentService.updateStudent(id, student, user)
				.switchIfEmpty(Mono.error(new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"No se pudo actualizar. Estudiante no encontrado con ID: " + id)))
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar estudiante")));
	}

	@Operation(summary = "Eliminar estudiante", description = "Elimina un estudiante por su ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Estudiante eliminado exitosamente"),
			@ApiResponse(responseCode = "404", description = "Estudiante no encontrado"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@DeleteMapping("/eliminar/{id}")
	public Mono<Void> eliminarUsuario(
			@Parameter(description = "ID del estudiante", required = true) @PathVariable String id) {
		return studentService.deleteStudent(id)
				.switchIfEmpty(Mono.error(new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"No se pudo eliminar. Estudiante no encontrado con ID: " + id)))
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar estudiante.")));
	}
}