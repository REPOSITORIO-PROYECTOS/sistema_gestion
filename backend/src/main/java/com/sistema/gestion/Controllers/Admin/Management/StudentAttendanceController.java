package com.sistema.gestion.Controllers.Admin.Management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.Models.Admin.Management.StudentAttendance;
import com.sistema.gestion.Services.Admin.Management.StudentAttendanceService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/asistencias")
public class StudentAttendanceController {

  @Autowired
  private final StudentAttendanceService studentAttendanceService;

  public StudentAttendanceController(StudentAttendanceService studentAttendanceService) {
    this.studentAttendanceService = studentAttendanceService;
  }

  @GetMapping("/")
  public Flux<StudentAttendance> findByMonth(@RequestParam Integer month, @RequestParam Integer year) {
    return studentAttendanceService.findByMonth(month, year)
        .onErrorResume(e -> Mono.error(new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener asistencias para el mes " + month + " anio:" + year)));
  }

  @PostMapping
  public Mono<StudentAttendance> takeAttendance(@RequestBody @Valid StudentAttendance studentAttendance) {
    return studentAttendanceService.takeAttendance(studentAttendance)
        .onErrorResume(e -> Mono.error(new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Error al tomar asistencia." + e.getMessage())));
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
