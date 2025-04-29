package com.sistema.gestion.Services.Admin.Management.VirtualCampus;

import com.sistema.gestion.Models.Admin.Management.VirtualCampus.CourseSection;
import com.sistema.gestion.Models.Admin.Management.VirtualCampus.CourseSubSection;
import com.sistema.gestion.Repositories.Admin.Management.VirtualCampus.CourseSectionRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

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

    public Flux<CourseSection> findAll(ServerWebExchange exchange) {
        // ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        // if (template == null) {
        //     return Flux.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        // }
        // return template.findAll(CourseSection.class);
        return courseSectionRepository.findAll();
    }

    public Mono<CourseSection> findById(String id) {
        // ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        // if (template == null) {
        //     return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        // }
        // return template.findById(id, CourseSection.class);
        return courseSectionRepository.findById(id);
    }

    public Mono<CourseSection> create(ServerWebExchange exchange, CourseSection courseSection) {
        // ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        // if (template == null) {
        //     return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        // }
        // return template.save(courseSection);
        return courseSectionRepository.save(courseSection);
    }

    public Mono<CourseSection> update(ServerWebExchange exchange, String id, CourseSection courseSection) {
        // ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        // if (template == null) {
        //     return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        // }
        // //TODO revisar LA actualizaicon de datos
        // courseSection.setId(id);
        // return template.save(courseSection);
        return courseSectionRepository.updateNameAndDescription(courseSection.getId(), courseSection.getName(), courseSection.getDescription());
    }

    public Mono<Void> delete(ServerWebExchange exchange, String id) {
        // ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        // if (template == null) {
        //     return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        // }
        //return template.deleteById(id, CourseSection.class);
        return courseSectionRepository.deleteById(id);
    }

    public Mono<CourseSection> addSubSection(ServerWebExchange exchange, String sectionId, CourseSubSection subSection) {
        // ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        // if (template == null) {
        //     return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        // }
        // return template.findById(sectionId, CourseSection.class)
        //         .flatMap(section -> {
        //             section.getSubSectionsIds().add(subSection.getId());
        //             courseSubSectionService.create(exchange,subSection);
        //             return template.save(section);
        //         });
        return courseSectionRepository.findById(sectionId)
                .flatMap(section -> {
                    section.getSubSectionsIds().add(subSection.getId());
                    courseSubSectionService.create(exchange,subSection);
                    return courseSectionRepository.save(section);
                });
    }
}
