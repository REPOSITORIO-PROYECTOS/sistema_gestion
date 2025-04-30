package com.sistema.gestion.Repositories.Admin.Management;
import com.sistema.gestion.Models.Admin.Management.GoogleDriveCredentials;

import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

@Document(collection = "google_drive_credentials")
public interface GoogleDriveCredentialsRepository extends ReactiveMongoRepository<GoogleDriveCredentials, String> {
    Mono<GoogleDriveCredentials> findById(String id);
}
