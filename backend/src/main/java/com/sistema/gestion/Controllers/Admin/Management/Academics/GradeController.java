package com.sistema.gestion.Controllers.Admin.Management.Academics;

import com.sistema.gestion.Models.Admin.Management.Grade;
import com.sistema.gestion.Services.Admin.Management.Academics.GradeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Grade> createGrade(ServerWebExchange exchange, @Valid @RequestBody Grade grade) {
        return gradeService.createGrade(grade);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Grade>> getGradeById(ServerWebExchange exchange, @PathVariable String id) {
        return gradeService.getGradeById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}/course/{courseId}")
    public Flux<Grade> getGradesByStudentAndCourse(ServerWebExchange exchange, @PathVariable String studentId, @PathVariable String courseId) {
        return gradeService.getGradesByStudentAndCourse(studentId, courseId);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Grade>> updateGrade(ServerWebExchange exchange, @PathVariable String id, @Valid @RequestBody Grade grade) {
        return gradeService.updateGrade(id, grade)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteGrade(ServerWebExchange exchange, @PathVariable String id) {
        return gradeService.deleteGrade(id);
    }
}

