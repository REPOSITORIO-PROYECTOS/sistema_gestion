package com.sistema.gestion.Controllers.Admin.Management.VirtualCampus;

import com.mongodb.client.result.DeleteResult;
import com.sistema.gestion.Models.Admin.Management.VirtualCampus.CourseSection;
import com.sistema.gestion.Models.Admin.Management.VirtualCampus.CourseSubSection;
import com.sistema.gestion.Services.Admin.Management.VirtualCampus.CourseSectionService;

import io.swagger.v3.oas.models.servers.Server;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ServerWebExchange;


@RestController
@RequestMapping("/api/course-sections")
public class CourseSectionController {

    private final CourseSectionService courseSectionService;

    public CourseSectionController(CourseSectionService courseSectionService) {
        this.courseSectionService = courseSectionService;
    }

    @GetMapping
    public Flux<CourseSection> getAllSections(ServerWebExchange exchange) {
        return courseSectionService.findAll(exchange);
    }

    @GetMapping("/getSectionById/{id}")
    public Mono<CourseSection> getSectionById(ServerWebExchange exchange, @PathVariable String id) {
        return courseSectionService.findById(exchange, id);
    }

    @PostMapping("/createSection")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CourseSection> createSection(ServerWebExchange exchange, @RequestBody CourseSection courseSection) {
        return courseSectionService.create(exchange, courseSection);
    }

    @PutMapping("/{id}")
    public Mono<CourseSection> updateSection(ServerWebExchange exchange, @PathVariable String id, @RequestBody CourseSection courseSection) {
        return courseSectionService.update(exchange, id, courseSection);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<DeleteResult> deleteSection(ServerWebExchange exchange, @PathVariable String id) {
        return courseSectionService.delete(exchange, id);
    }
    
    @PostMapping("/{id}/subseccion")
    public Mono<CourseSection> addSubSection(ServerWebExchange exchange, @PathVariable String id, @RequestBody CourseSubSection subSection) {
        return courseSectionService.addSubSection(exchange, id, subSection);
    }
}
