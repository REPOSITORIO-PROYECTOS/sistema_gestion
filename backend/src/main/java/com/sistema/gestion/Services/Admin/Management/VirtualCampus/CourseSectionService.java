package com.sistema.gestion.Services.Admin.Management.VirtualCampus;

import com.sistema.gestion.Models.Admin.Management.VirtualCampus.CourseSection;
import com.sistema.gestion.Models.Admin.Management.VirtualCampus.CourseSubSection;
import com.sistema.gestion.Repositories.Admin.Management.VirtualCampus.CourseSectionRepository;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Service
public class CourseSectionService {

    private final CourseSectionRepository courseSectionRepository;
    private final CourseSubSectionService courseSubSectionService;

    public CourseSectionService(CourseSectionRepository courseSectionRepository, CourseSubSectionService courseSubSectionService) {
        this.courseSectionRepository = courseSectionRepository;
        this.courseSubSectionService = courseSubSectionService;
    }

    public Flux<CourseSection> findAll() {
        return courseSectionRepository.findAll();
    }

    public Mono<CourseSection> findById(String id) {
        return courseSectionRepository.findById(id);
    }

    public Mono<CourseSection> create(CourseSection courseSection) {
        return courseSectionRepository.save(courseSection);
    }

    public Mono<CourseSection> update(String id, CourseSection courseSection) {
        courseSection.setId(id);
        return courseSectionRepository.save(courseSection);
    }

    public Mono<Void> delete(String id) {
        return courseSectionRepository.deleteById(id);
    }

    public Mono<CourseSection> addSubSection(String sectionId, CourseSubSection subSection) {
        return courseSectionRepository.findById(sectionId)
                .flatMap(section -> {
                    section.getSubSectionsIds().add(subSection.getId());
                    courseSubSectionService.create(subSection);
                    return courseSectionRepository.save(section);
                });
    }
}
