package com.sistema.gestion.Services.Admin.Management;

import java.time.LocalDate;
import java.time.YearMonth;

import com.sistema.gestion.Models.Admin.Management.StudentAttendance;
import com.sistema.gestion.Repositories.Admin.Management.StudentAttendanceRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class StudentAttendanceService {
    
    private final StudentAttendanceRepository studentAttendanceRepository;

    public StudentAttendanceService(StudentAttendanceRepository studentAttendanceRepository) {
        this.studentAttendanceRepository = studentAttendanceRepository;
    }

    public Flux<StudentAttendance> findByMonth(Integer month, Integer year) {
        LocalDate startMonth = YearMonth.of(year, month).atDay(1);
        LocalDate endMonth = startMonth.plusMonths(1);
        return studentAttendanceRepository.findByMonth(startMonth, endMonth);
    }

    public Mono<StudentAttendance> takeAttendance(StudentAttendance studentAttendance) {
        return studentAttendanceRepository.save(studentAttendance);
    }

    public Mono<StudentAttendance> updateAttendance(StudentAttendance studentAttendance) {
        return studentAttendanceRepository.findById(studentAttendance.getId())
                .flatMap(existingAttendance -> {
                    existingAttendance.setStudentsIds(studentAttendance.getStudentsIds());
                    existingAttendance.setAttendanceStatus(studentAttendance.getAttendanceStatus());
                    return studentAttendanceRepository.save(existingAttendance);
                });
    }

}
