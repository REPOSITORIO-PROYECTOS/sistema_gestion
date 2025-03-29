package com.sistema.gestion.Controllers.Admin.Management.VirtualCampus;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerWebExchange;

import com.mongodb.client.result.DeleteResult;
import com.sistema.gestion.Models.Admin.Management.VirtualCampus.File;
import com.sistema.gestion.Services.Admin.Management.VirtualCampus.FileService;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    public Flux<File> getAllFiles(ServerWebExchange exchange) {
        return fileService.findAll(exchange);
    }

    @GetMapping("/{id}")
    public Mono<File> getFileById(ServerWebExchange exchange, @PathVariable String id) {
        return fileService.findById(exchange, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<File> createFile(ServerWebExchange exchange, @RequestBody File file) {
        return fileService.create(exchange, file);
    }

    //TODO: Implementar subida de archivos a Google Drive
    // @PostMapping(path = "/subir", consumes = "multipart/form-data")
    // public Mono<ResponseEntity<String>> subirArchivo(ServerWebExchange exchange, @RequestPart("file") Mono<MultipartFile> file) {
    //     return file
    //         .flatMap(filePart -> fileService.subirArchivoADrive(filePart)) // Sube el archivo a Drive
    //         .map((url) -> ResponseEntity.ok("Archivo subido: " + url));
    // }

    @PutMapping("/{id}")
    public Mono<File> updateFile(ServerWebExchange exchange, @PathVariable String id, @RequestBody File file) {
        return fileService.update(exchange, id, file);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<DeleteResult> deleteFile(ServerWebExchange exchange, @PathVariable String id) {
        return fileService.delete(exchange, id);
    }
}
