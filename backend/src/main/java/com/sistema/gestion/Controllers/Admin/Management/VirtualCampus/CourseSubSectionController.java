package com.sistema.gestion.Controllers.Admin.Management.VirtualCampus;

import com.sistema.gestion.Models.Admin.Management.VirtualCampus.CourseSubSection;
import com.sistema.gestion.Services.Admin.Management.VirtualCampus.CourseSubSectionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/course-subsections")
public class CourseSubSectionController {

    private final CourseSubSectionService courseSubSectionService;

    public CourseSubSectionController(CourseSubSectionService courseSubSectionService) {
        this.courseSubSectionService = courseSubSectionService;
    }

    @GetMapping
    public Flux<CourseSubSection> getAllSubSections(ServerWebExchange exchange) {
        return courseSubSectionService.findAll(exchange);
    }

    @GetMapping("/{id}")
    public Mono<CourseSubSection> getSubSectionById(ServerWebExchange exchange, @PathVariable String id) {
        return courseSubSectionService.findById(exchange, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CourseSubSection> createSubSection(ServerWebExchange exchange, @RequestBody CourseSubSection courseSubSection) {
        return courseSubSectionService.create(exchange, courseSubSection);
    }

    @PutMapping("/{id}")
    public Mono<CourseSubSection> updateSubSection(ServerWebExchange exchange, @PathVariable String id, @RequestBody CourseSubSection courseSubSection) {
        return courseSubSectionService.update(exchange,id, courseSubSection);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteSubSection(ServerWebExchange exchange, @PathVariable String id) {
        return courseSubSectionService.delete(exchange, id);
    }

    @PostMapping("/{subSectionId}/addFile")
    public Mono<ResponseEntity<String>> addFile(ServerWebExchange exchange, @PathVariable String subSectionId, @RequestPart("fileName") String name, @RequestPart("newFile") Mono<FilePart> file) {
        return courseSubSectionService.addFile(exchange, subSectionId, name, file).map(id -> ResponseEntity.ok("Id del archivo subido: " + id));
    }
}
