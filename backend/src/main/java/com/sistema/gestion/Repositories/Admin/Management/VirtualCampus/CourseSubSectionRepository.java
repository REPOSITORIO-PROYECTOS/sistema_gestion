package com.sistema.gestion.Repositories.Admin.Management.VirtualCampus;

import com.sistema.gestion.Models.Admin.Management.VirtualCampus.CourseSubSection;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseSubSectionRepository extends ReactiveMongoRepository<CourseSubSection, String> {
}