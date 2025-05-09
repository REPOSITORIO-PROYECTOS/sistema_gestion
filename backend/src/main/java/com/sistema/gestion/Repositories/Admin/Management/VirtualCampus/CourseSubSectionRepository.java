package com.sistema.gestion.Repositories.Admin.Management.VirtualCampus;

import com.sistema.gestion.Models.Admin.Management.VirtualCampus.CourseSubSection;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseSubSectionRepository extends ReactiveMongoRepository<CourseSubSection, String> {
    Flux<CourseSubSection> findBySectionId(String sectionId);

    @Query("{ '_id': ?0 }")
    Mono<CourseSubSection> updateTitleById(String id, String title);
    
    @Query("{ '_id': ?0 }")
    Mono<CourseSubSection> updateBodyById(String id, String body);
}