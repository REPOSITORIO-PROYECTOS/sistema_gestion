package com.sistema.gestion.Repositories.Admin.Management;

import com.sistema.gestion.Models.Admin.Management.Grade;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface GradeRepository extends ReactiveMongoRepository<Grade, String> {
    Flux<Grade> findAllByStudentId(String studentId);
    Flux<Grade> findAllByStudentIdAndCourseId(String studentId, String courseId);
}

