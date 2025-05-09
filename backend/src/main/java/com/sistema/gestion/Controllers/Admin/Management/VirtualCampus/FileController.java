package com.sistema.gestion.Controllers.Admin.Management.VirtualCampus;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;

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

    @GetMapping("/obtener-todos")
    public Flux<File> getAllFiles(ServerWebExchange exchange) {
        return fileService.findAll(exchange);
    }

    @GetMapping("/obtener/{id}")
    public Mono<File> getFileById(ServerWebExchange exchange, @PathVariable String id) {
        return fileService.findById(exchange, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<File> createFile(ServerWebExchange exchange, @RequestBody File file) {
        return fileService.create(exchange, file);
    }

    @PostMapping(path = "/subir", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> subirArchivo(ServerWebExchange exchange, @RequestPart(value="file") Mono<FilePart> file) throws IOException {
        return fileService.saveFile(exchange, file) // Sube el archivo a Drive
            .map((url) -> ResponseEntity.ok("Archivo subido: " + url));
    }

    @PutMapping("/update/{id}")
    public Mono<File> updateFile(ServerWebExchange exchange, @PathVariable String id, @RequestPart Mono<FilePart> file) {
        return fileService.update(exchange, id, file);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteFile(ServerWebExchange exchange, @PathVariable String id) {
        return fileService.delete(exchange, id);
    }
}
