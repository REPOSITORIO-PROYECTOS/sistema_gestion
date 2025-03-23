package com.sistema.gestion.Services.Admin.Management.VirtualCampus;

import com.sistema.gestion.Models.Admin.Management.VirtualCampus.CourseSubSection;
import com.sistema.gestion.Repositories.Admin.Management.VirtualCampus.CourseSubSectionRepository;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Service
public class CourseSubSectionService {

    private final CourseSubSectionRepository courseSubSectionRepository;

    public CourseSubSectionService(CourseSubSectionRepository courseSubSectionRepository) {
        this.courseSubSectionRepository = courseSubSectionRepository;
    }

    public Flux<CourseSubSection> findAll() {
        return courseSubSectionRepository.findAll();
    }

    public Mono<CourseSubSection> findById(String id) {
        return courseSubSectionRepository.findById(id);
    }

    public Mono<CourseSubSection> create(CourseSubSection courseSubSection) {
        return courseSubSectionRepository.save(courseSubSection);
    }

    public Mono<CourseSubSection> update(String id, CourseSubSection courseSubSection) {
        courseSubSection.setId(id);
        return courseSubSectionRepository.save(courseSubSection);
    }

    public Mono<Void> delete(String id) {
        return courseSubSectionRepository.deleteById(id);
    }
}
