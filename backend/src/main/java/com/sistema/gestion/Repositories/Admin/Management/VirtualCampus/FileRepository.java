package com.sistema.gestion.Repositories.Admin.Management.VirtualCampus;

import com.sistema.gestion.Models.Admin.Management.VirtualCampus.File;

import reactor.core.publisher.Flux;

import java.util.List;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends ReactiveMongoRepository<File, String> {
    Flux<File> findByIdIn(List<String> ids);
    Flux<File> findBySubSectionId(String subSectionId);
}
