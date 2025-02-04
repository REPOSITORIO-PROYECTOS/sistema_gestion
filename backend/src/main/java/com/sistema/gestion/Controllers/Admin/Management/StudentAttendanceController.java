package com.sistema.gestion.Controllers.Admin.Management;

import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/api/asistencias")
@Tag(name = "Asistencias", description = "Registro de asistencias")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StudentAttendanceController {

  private final StudentAttendanceService studentAttendanceService;

  @GetMapping("/{courseId}")
  public Flux<StudentAttendance> findByMonthAndCourse(
      @RequestParam Integer month,
      @RequestParam Integer year,
      @PathVariable String courseId) {
    return studentAttendanceService.findByMonthAndCourse(month, year, courseId)
        .onErrorResume(e -> Mono.error(new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Error al obtener asistencias del curso " + courseId +
                " para el mes " + month + " del año " + year)));
  }

  @PostMapping
  public Mono<StudentAttendance> takeAttendance(@RequestBody @Valid StudentAttendance studentAttendance) {
    return studentAttendanceService.takeAttendance(studentAttendance)
        .onErrorResume(e -> Mono.error(new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Error al tomar asistencia.")));
  }

  @PutMapping("/modificar-asistencia")
  public Mono<StudentAttendance> updateAttendance(@RequestBody @Valid StudentAttendance studentAttendance) {
    return studentAttendanceService.updateAttendance(studentAttendance)
        .switchIfEmpty(Mono.error(new ResponseStatusException(
            HttpStatus.NOT_FOUND, "No se encontró asistencia para actualizar.")))
        .onErrorResume(e -> Mono.error(new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, "Error al modificar asistencia.")));
  }
}