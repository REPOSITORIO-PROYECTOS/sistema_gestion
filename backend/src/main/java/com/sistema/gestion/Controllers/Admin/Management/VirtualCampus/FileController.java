package com.sistema.gestion.Controllers.Admin.Management.VirtualCampus;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public Flux<File> getAllFiles() {
        return fileService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<File> getFileById(@PathVariable String id) {
        return fileService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<File> createFile(@RequestBody File file) {
        return fileService.create(file);
    }

    @PostMapping("/subir")
    public Mono<ResponseEntity<File>> subirArchivo(@RequestPart("file") MultipartFile file) {
        return fileService.subirArchivoADrive(file)
                .flatMap(url -> fileService.guardarEnMongo(new File(url)))
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<File> updateFile(@PathVariable String id, @RequestBody File file) {
        return fileService.update(id, file);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteFile(@PathVariable String id) {
        return fileService.delete(id);
    }
}
