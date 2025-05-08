package com.sistema.gestion.Controllers.Admin.Management;

import java.util.Map;

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

import com.sistema.gestion.DTO.AllContentCourseDTO;
import com.sistema.gestion.DTO.PagedResponse;
import com.sistema.gestion.Models.Admin.Management.Course;
import com.sistema.gestion.Services.Admin.Management.CourseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/cursos")
@Tag(name = "Course Controller", description = "Operaciones relacionadas con los cursos: altas, bajas, modificaciones, y registro de estudiantes a los cursos existentes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CourseController {

	private final CourseService courseService;

	@GetMapping("/paged")
	public Mono<PagedResponse<Course>> getCoursesPaged(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String keyword) {
		return courseService.getCoursesPaged(page, size, keyword);
	}

	@GetMapping("/obtenerPorProfesor/{professorId}")
	@Operation(summary = "Obtener cursos por ID de profesor", description = "Obtiene los cursos asociados a un profesor por su ID.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Cursos obtenidos exitosamente", content = @Content(schema = @Schema(implementation = Course.class))),
			@ApiResponse(responseCode = "404", description = "No se encontraron cursos para el profesor especificado")
	})
	public Flux<Course> getCoursesByProfessorId(
			@Parameter(description = "ID del profesor", required = true) @PathVariable String professorId) {
		return courseService.getCoursesByProfessorId(professorId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron cursos para el profesor especificado")));
	}

	@Operation(summary = "Registrar un nuevo curso", description = "Registra un nuevo curso en el sistema.", responses = {
			@ApiResponse(responseCode = "201", description = "Curso registrado exitosamente."),
			@ApiResponse(responseCode = "400", description = "Error en los datos proporcionados.")
	})
	@PostMapping
	public Mono<ResponseEntity<Course>> createCourse(
			Authentication auth,
			@RequestBody @Valid Course course
	) {

		String user = auth.getName();

		return courseService.saveCourse(course, user)
				.map(savedCourse -> ResponseEntity.status(HttpStatus.CREATED).body(savedCourse))
				.onErrorMap(e -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Error al registrar el curso"));
	}

	@Operation(summary = "Actualizar un curso", description = "Modifica los detalles de un curso existente.", responses = {
			@ApiResponse(responseCode = "200", description = "Curso actualizado exitosamente."),
			@ApiResponse(responseCode = "400", description = "Error en los datos proporcionados."),
			@ApiResponse(responseCode = "404", description = "No se encontró el curso a actualizar.")
	})

	@PutMapping("/{courseId}")
	public Mono<ResponseEntity<Course>> updateCourse(
			Authentication auth,
			@PathVariable @Parameter(description = "ID del curso a actualizar", required = true) String courseId,
			@RequestBody @Valid Course course) {

		String user = auth.getName();

		return courseService.updateCourse(course, courseId, user)
				.map(savedCourse -> ResponseEntity.status(HttpStatus.CREATED).body(savedCourse))
				.onErrorMap(e -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Error al modificar o actualizar el curso."));
	}

	@GetMapping("/obtenerContenido/{courseId}")
	@Operation(summary = "Obtener contenido de un curso", description = "Obtiene el contenido de un curso por su ID.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Contenido del curso obtenido exitosamente", content = @Content(schema = @Schema(implementation = Course.class))),
			@ApiResponse(responseCode = "404", description = "Curso no encontrado")
	})
	public Mono<AllContentCourseDTO> getCourseContent(
			@Parameter(description = "ID del curso", required = true) @PathVariable String courseId) {
		return courseService.getCourseContent(courseId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado")));
	}

	@Operation(summary = "Inscribir un estudiante en un curso", description = "Permite inscribir a un estudiante en el curso especificado mediante su ID.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Estudiante inscrito exitosamente", content = @Content(schema = @Schema(implementation = Course.class))),
			@ApiResponse(responseCode = "400", description = "El campo 'studentId' es obligatorio"),
			@ApiResponse(responseCode = "404", description = "No se pudo inscribir al estudiante en el curso")
	})
	@PutMapping("/inscripcion/{courseId}")
	public Mono<Course> courseScheduling(
			@Parameter(description = "ID del curso al que se desea inscribir", required = true) @PathVariable String courseId,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Cuerpo de la solicitud que contiene el ID del estudiante", required = true, content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"studentId\": \"idDeEjemplo\"}"))) @RequestBody Map<String, String> requestBody) {
		String studentId = requestBody.get("studentId");
		if (studentId == null) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo 'studentId' es obligatorio"));
		}
		return courseService.registerStudentInCourse(courseId, studentId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,  "No se pudo inscribir al estudiante en el curso")));
	}

	@Operation(summary = "Eliminar un estudiante de un curso", description = "Permite eliminar la inscripción de un estudiante en el curso especificado mediante su ID.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Estudiante eliminado exitosamente", content = @Content(schema = @Schema(implementation = Course.class))),
			@ApiResponse(responseCode = "400", description = "El campo 'studentId' es obligatorio"),
			@ApiResponse(responseCode = "404", description = "No se pudo eliminar al estudiante del curso")
	})
	@PutMapping("/desinscripcion/{courseId}")
	public Mono<Course> removeStudentFromCourse(
			@Parameter(description = "ID del curso del que se desea eliminar la inscripción", required = true) @PathVariable String courseId,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Cuerpo de la solicitud que contiene el ID del estudiante", required = true, content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"studentId\": \"idDeEjemplo\"}"))) @RequestBody Map<String, String> requestBody) {
		String studentId = requestBody.get("studentId");
		if (studentId == null) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"El campo 'studentId' es obligatorio"));
		}
		return courseService.removeStudentFromCourse(courseId, studentId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
						"No se pudo eliminar al estudiante del curso")));
	}

	@Operation(summary = "Eliminar un curso", description = "Elimina un curso por su ID.", responses = {
			@ApiResponse(responseCode = "204", description = "Curso eliminado exitosamente."),
			@ApiResponse(responseCode = "404", description = "No se encontró el curso.")
	})
	@DeleteMapping("/{courseId}")
	public Mono<ResponseEntity<Void>> deleteCourse(
			@PathVariable @Parameter(description = "ID del curso a eliminar", required = true) String courseId) {
		return courseService.deleteCourse(courseId)
				.then(Mono.just(ResponseEntity.noContent().<Void>build()))
				.onErrorMap(e -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Error al eliminar el curso con ID: " + courseId));
	}
}
