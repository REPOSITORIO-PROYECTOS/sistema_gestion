package com.sistema.gestion.Services.Admin.Management.VirtualCampus;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.mongodb.client.result.DeleteResult;
import com.sistema.gestion.Models.Admin.Management.VirtualCampus.File;
import com.sistema.gestion.Repositories.Admin.Management.VirtualCampus.FileRepository;

import io.swagger.v3.oas.models.servers.Server;

import java.util.Collections;

import org.springframework.boot.autoconfigure.mustache.MustacheProperties.Reactive;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final Drive googleDriveService;

    public FileService(FileRepository filesRepository, Drive googleDriveService) {
        this.fileRepository = filesRepository;
        this.googleDriveService = googleDriveService;
    }

    public Flux<File> findAll(ServerWebExchange exchange) {
        ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        if (template == null) {
            return Flux.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        }
        return template.findAll(File.class);
    }

    public Mono<File> findById(ServerWebExchange exchange, String id) {
        ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        if (template == null) {
            return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        }
        return template.findById(id, File.class);
    }

    public Mono<File> create(ServerWebExchange exchange, File file) {
        ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        if (template == null) {
            return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        }
        return template.save(file);
    }

    public Mono<String> subirArchivoADrive(MultipartFile file) {
        return Mono.fromCallable(() -> {
            // Crear metadatos del archivo en Google Drive
            com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
            fileMetadata.setName(file.getOriginalFilename());
            fileMetadata.setParents(Collections.singletonList("<FOLDER_ID>")); 

            // Crear el contenido del archivo
            AbstractInputStreamContent fileContent = new InputStreamContent(
                    file.getContentType(), file.getInputStream());

            // Subir el archivo a Google Drive
            com.google.api.services.drive.model.File googleFile = googleDriveService.files()
                    .create(fileMetadata, fileContent)
                    .setFields("id, webViewLink")
                    .execute();

            return googleFile.getWebViewLink(); // Retornar la URL del archivo en Drive
        });
    }

    public Mono<String> saveFile(ServerWebExchange exchange, String name, MultipartFile file) {
        ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        if (template == null) {
            return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        }
        return subirArchivoADrive(file).flatMap(link -> {
            File fileToSave = new File(name, link);
            return template.save(fileToSave).flatMap(savedFile -> Mono.just(savedFile.getId()));
        });
    }

    public Mono<File> update(ServerWebExchange exchange, String id, File file) {
        ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        if (template == null) {
            return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        }
        file.setId(id);
        return template.save(file);
    }

    public Mono<DeleteResult> delete(ServerWebExchange exchange, String id) {
        ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        if (template == null) {
            return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        }
        return template.remove(File.class, id);
    }
}
