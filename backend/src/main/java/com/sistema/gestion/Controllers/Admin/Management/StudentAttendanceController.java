package com.sistema.gestion.Controllers.Admin.Management;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistema.gestion.Models.Admin.Management.StudentAttendance;
import com.sistema.gestion.Services.Admin.Management.StudentAttendanceService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/asistencias")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StudentAttendanceController {
    
    private final StudentAttendanceService studentAttendanceService;

    @GetMapping("/")
    public Flux<StudentAttendance> findByMonth(@PathVariable Integer month, Integer year) {
        return studentAttendanceService.findByMonth(month, year);
    }

    @PostMapping("/tomar-asistencia")
    public Mono<StudentAttendance> takeAttendance(@RequestBody @Valid StudentAttendance studentAttendance) {
        return studentAttendanceService.takeAttendance(studentAttendance);
    }

    @PutMapping("/modificar-asistencia")
    public Mono<StudentAttendance> updateAttendance(@RequestBody @Valid StudentAttendance studentAttendance) {
        return studentAttendanceService.updateAttendance(studentAttendance);
    }

}
