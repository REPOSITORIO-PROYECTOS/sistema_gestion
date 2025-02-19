package com.sistema.gestion.Services.Admin.Management;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Set;

import com.sistema.gestion.Services.Profiles.UserService;
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
    private final UserService userService;

    // ? ==================== MÉTODOS PÚBLICOS ====================

    public Flux<StudentAttendance> findByMonthAndCourse(Integer month, Integer year, String courseId) {
        LocalDate startMonth = YearMonth.of(year, month).atDay(1);
        LocalDate endMonth = startMonth.plusMonths(1);
        return studentAttendanceRepository.findByMonthAndCourse(startMonth, endMonth, courseId);
    }


    public Mono<StudentAttendance> takeAttendance(StudentAttendance studentAttendance, String username) {
        return userService.getFullName(username)
                .flatMap(fullName -> {
                    studentAttendance.setCreatedBy(fullName);
                    studentAttendance.setCreatedAt(LocalDateTime.now());
                    return validateCourseAndStudents(studentAttendance)
                            .then(studentAttendanceRepository.save(studentAttendance));
                });
    }

    public Mono<StudentAttendance> updateAttendance(StudentAttendance studentAttendance, String username) {
        return userService.getFullName(username)
                .flatMap(fullName -> studentAttendanceRepository.findById(studentAttendance.getId())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "No se encontró la asistencia con ID: " + studentAttendance.getId())))
                        .flatMap(existingAttendance -> {
                            existingAttendance.setStudentsIds(studentAttendance.getStudentsIds());
                            existingAttendance.setAttendanceStatus(studentAttendance.getAttendanceStatus());
                            existingAttendance.setUpdatedAt(LocalDateTime.now());
                            existingAttendance.setModifiedBy(fullName);
                            return studentAttendanceRepository.save(existingAttendance);
                        }));
    }

    // ? ==================== MÉTODOS PRIVADOS ====================

    private Mono<Void> validateCourseAndStudents(StudentAttendance studentAttendance) {
        return courseRepo.findById(studentAttendance.getCourseId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró el curso con ID: " + studentAttendance.getCourseId())))
                .then(validateStudents(studentAttendance.getStudentsIds()));
    }

    private Mono<Void> validateStudents(Set<String> studentIds) {
        return Flux.fromIterable(studentIds)
                .flatMap(studentId -> studentRepo.findById(studentId)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "El estudiante con ID: " + studentId + " no existe"))))
                .then();
    }
}