package com.sistema.gestion.Services.Admin.Management.VirtualCampus;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.sistema.gestion.Models.Admin.Management.VirtualCampus.File;
import com.sistema.gestion.Repositories.Admin.Management.VirtualCampus.FileRepository;

import java.util.Collections;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public Flux<File> findAll() {
        return fileRepository.findAll();
    }

    public Mono<File> findById(String id) {
        return fileRepository.findById(id);
    }

    public Mono<File> create(File file) {
        return fileRepository.save(file);
    }

    public Mono<String> subirArchivoADrive(MultipartFile file) {
        return Mono.fromCallable(() -> {
            // Crear metadatos del archivo en Google Drive
            com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
            fileMetadata.setName(file.getOriginalFilename());
            fileMetadata.setParents(Collections.singletonList("https://drive.google.com/drive/folders/1eUrt3JWRauSeccLMWsI5DqCaRPIdEAv2?usp=drive_link"));

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

    public Mono<String> saveFile(String name, MultipartFile file) {
        return subirArchivoADrive(file).flatMap(link -> {
            File fileToSave = new File(name, link);
            return fileRepository.save(fileToSave).flatMap(savedFile -> Mono.just(savedFile.getId()));
        });
    }

    public Mono<File> update(String id, File file) {
        file.setId(id);
        return fileRepository.save(file);
    }

    public Mono<Void> delete(String id) {
        return fileRepository.deleteById(id);
    }
}
