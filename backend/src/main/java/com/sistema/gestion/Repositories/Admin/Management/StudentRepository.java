package com.sistema.gestion.Repositories.Admin.Management;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.sistema.gestion.Models.Profiles.Student;

@Repository
public interface StudentRepository extends ReactiveMongoRepository<Student, String> {

}
