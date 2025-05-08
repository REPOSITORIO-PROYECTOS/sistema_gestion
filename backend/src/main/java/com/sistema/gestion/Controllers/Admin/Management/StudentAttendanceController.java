package com.sistema.gestion.Controllers.Admin.Management;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.Models.Admin.Management.StudentAttendance;
import com.sistema.gestion.Services.Admin.Management.StudentAttendanceService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/asistencias")
@Tag(name = "Student Attendance Controller", description = "Registro de asistencias")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StudentAttendanceController {

	private final StudentAttendanceService studentAttendanceService;

	@Operation(summary = "Obtener asistencias por mes y curso", description = "Obtiene las asistencias de un curso para un mes y año específicos")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Asistencias obtenidas exitosamente"),
			@ApiResponse(responseCode = "404", description = "Curso no encontrado"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@GetMapping("/{courseId}")
	public Flux<StudentAttendance> findByMonthAndCourse(
			@Parameter(description = "Mes de las asistencias", required = true) @RequestParam Integer month,
			@Parameter(description = "Año de las asistencias", required = true) @RequestParam Integer year,
			@Parameter(description = "ID del curso", required = true) @PathVariable String courseId) {
		return studentAttendanceService.findByMonthAndCourse(month, year, courseId)
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Error al obtener asistencias del curso " + courseId +
								" para el mes " + month + " del año " + year)));
	}

	@Operation(summary = "Tomar asistencia", description = "Registra la asistencia de un estudiante")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Asistencia registrada exitosamente"),
			@ApiResponse(responseCode = "400", description = "Datos de la asistencia inválidos"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@PostMapping("/tomar-asistencia")
	public Mono<StudentAttendance> takeAttendance(
			Authentication auth,
			@Parameter(description = "Datos de la asistencia", required = true) @RequestBody StudentAttendance studentAttendance) {

		String user = auth.getName();

		return studentAttendanceService.takeAttendance(studentAttendance, user)
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.BAD_REQUEST, "Error al tomar asistencia." + e.getMessage())));
	}

	@Operation(summary = "Modificar asistencia", description = "Actualiza la asistencia de un estudiante")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Asistencia modificada exitosamente"),
			@ApiResponse(responseCode = "404", description = "Asistencia no encontrada"),
			@ApiResponse(responseCode = "400", description = "Datos de la asistencia inválidos"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@PutMapping("/modificar-asistencia")
	public Mono<StudentAttendance> updateAttendance(
			Authentication auth,
			@Parameter(description = "Datos de la asistencia a actualizar", required = true) @RequestBody @Valid StudentAttendance studentAttendance) {

		String user = auth.getName();

		return studentAttendanceService.updateAttendance(studentAttendance, user)
				.switchIfEmpty(Mono.error(new ResponseStatusException(
						HttpStatus.NOT_FOUND, "No se encontró asistencia para actualizar.")))
				.onErrorResume(e -> Mono.error(new ResponseStatusException(
						HttpStatus.INTERNAL_SERVER_ERROR, "Error al modificar asistencia.")));
	}
}