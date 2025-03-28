package com.sistema.gestion.Repositories.Admin.Management.VirtualCampus;

import com.sistema.gestion.Models.Admin.Management.VirtualCampus.CourseSection;

import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseSectionRepository extends ReactiveMongoRepository<CourseSection, String> {
    Mono<CourseSection> findById(String id);
}