package com.sistema.gestion.Repositories.Admin.Management.VirtualCampus;

import com.sistema.gestion.Models.Admin.Management.VirtualCampus.CourseSection;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseSectionRepository extends ReactiveMongoRepository<CourseSection, String> {
    Mono<CourseSection> findById(String id);
    Flux<CourseSection> findByCourseId(String id);
    @Query("{ '_id': ?0 }")
    Mono<CourseSection> updateNameAndDescription(String id, String name, String description);
}