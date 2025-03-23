package com.sistema.gestion.Controllers.Admin.Management.VirtualCampus;

import com.sistema.gestion.Models.Admin.Management.VirtualCampus.CourseSubSection;
import com.sistema.gestion.Services.Admin.Management.VirtualCampus.CourseSubSectionService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
    public Flux<CourseSubSection> getAllSubSections() {
        return courseSubSectionService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<CourseSubSection> getSubSectionById(@PathVariable String id) {
        return courseSubSectionService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CourseSubSection> createSubSection(@RequestBody CourseSubSection courseSubSection) {
        return courseSubSectionService.create(courseSubSection);
    }

    @PutMapping("/{id}")
    public Mono<CourseSubSection> updateSubSection(@PathVariable String id, @RequestBody CourseSubSection courseSubSection) {
        return courseSubSectionService.update(id, courseSubSection);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteSubSection(@PathVariable String id) {
        return courseSubSectionService.delete(id);
    }
}
