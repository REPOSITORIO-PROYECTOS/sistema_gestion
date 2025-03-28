package com.sistema.gestion.Controllers.Admin.Management.VirtualCampus;

import com.sistema.gestion.Models.Admin.Management.VirtualCampus.CourseSection;
import com.sistema.gestion.Models.Admin.Management.VirtualCampus.CourseSubSection;
import com.sistema.gestion.Services.Admin.Management.VirtualCampus.CourseSectionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/course-sections")
public class CourseSectionController {

    private final CourseSectionService courseSectionService;

    public CourseSectionController(CourseSectionService courseSectionService) {
        this.courseSectionService = courseSectionService;
    }

    @GetMapping
    public Flux<CourseSection> getAllSections() {
        return courseSectionService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<CourseSection> getSectionById(@PathVariable String id) {
        return courseSectionService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CourseSection> createSection(@RequestBody CourseSection courseSection) {
        return courseSectionService.create(courseSection);
    }

    @PutMapping("/{id}")
    public Mono<CourseSection> updateSection(@PathVariable String id, @RequestBody CourseSection courseSection) {
        return courseSectionService.update(id, courseSection);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteSection(@PathVariable String id) {
        return courseSectionService.delete(id);
    }
    
    @PostMapping("/{id}/subseccion")
    public Mono<CourseSection> addSubSection(@PathVariable String id, @RequestBody CourseSubSection subSection) {
        return courseSectionService.addSubSection(id, subSection);
    }
}
