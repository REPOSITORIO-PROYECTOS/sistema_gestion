package com.sistema.gestion.Services.Admin.Management;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.Models.Admin.Management.StudentAttendance;
import com.sistema.gestion.Repositories.Admin.Management.CourseRepository;
import com.sistema.gestion.Repositories.Admin.Management.StudentAttendanceRepository;
import com.sistema.gestion.Repositories.Profiles.StudentRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StudentAttendanceService {

    private final StudentAttendanceRepository studentAttendanceRepository;
    private final CourseRepository courseRepo;
    private final StudentRepository studentRepo;

    public Flux<StudentAttendance> findByMonth(Integer month, Integer year) {
        LocalDate startMonth = YearMonth.of(year, month).atDay(1);
        LocalDate endMonth = startMonth.plusMonths(1);
        return studentAttendanceRepository.findByMonth(startMonth, endMonth);
    }

    public Mono<StudentAttendance> takeAttendance(StudentAttendance studentAttendance) {
        return courseRepo.findById(studentAttendance.getCourseId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró el curso con ID: " + studentAttendance.getCourseId())))
                .flatMap(course -> {
                    return validateStudents(studentAttendance.getStudentsIds())
                            .then(studentAttendanceRepository.save(studentAttendance)); // Guardar asistencia
                });
    }

    public Mono<StudentAttendance> updateAttendance(StudentAttendance studentAttendance) {
        return studentAttendanceRepository.findById(studentAttendance.getId())
                .flatMap(existingAttendance -> {
                    existingAttendance.setStudentsIds(studentAttendance.getStudentsIds());
                    existingAttendance.setAttendanceStatus(studentAttendance.getAttendanceStatus());
                    return studentAttendanceRepository.save(existingAttendance);
                });
    }

    // Metodos Auxiliares locales:
    private Mono<Void> validateStudents(Set<String> studentIds) {
        return Flux.fromIterable(studentIds)
                .flatMap(studentId -> studentRepo.findById(studentId) // Verificar cada estudiante en la base de datos
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "El estudiante con ID: " + studentId + " no existe"))))
                .then();
    }
}