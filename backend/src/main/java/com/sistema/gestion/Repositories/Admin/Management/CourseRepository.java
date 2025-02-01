package com.sistema.gestion.Repositories.Admin.Management;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import org.springframework.stereotype.Repository;

import com.sistema.gestion.Models.Admin.Management.Course;

import reactor.core.publisher.Flux;

@Repository
public interface CourseRepository extends ReactiveMongoRepository<Course, String> {
    @Query("{ $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } } ] }")
    Flux<Course> findByKeyword(String keyword);

    Flux<Course> findAll();

}