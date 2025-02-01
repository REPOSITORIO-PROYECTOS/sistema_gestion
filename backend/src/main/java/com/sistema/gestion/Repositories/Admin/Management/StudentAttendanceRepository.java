package com.sistema.gestion.Repositories.Admin.Management;

import java.time.LocalDate;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.sistema.gestion.Models.Admin.Management.StudentAttendance;

import reactor.core.publisher.Flux;

@Repository
public interface StudentAttendanceRepository extends ReactiveMongoRepository<StudentAttendance, String> {

    @Query("{ 'attendanceDate' : { $gte : ?0 , $lte : ?1 } }")
    public Flux<StudentAttendance> findByMonth(LocalDate startMonth, LocalDate endMonth);

}