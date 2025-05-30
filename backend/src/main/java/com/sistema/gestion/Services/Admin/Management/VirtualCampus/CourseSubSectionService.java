package com.sistema.gestion.Services.Admin.Management.VirtualCampus;

import com.sistema.gestion.Models.Admin.Management.VirtualCampus.CourseSubSection;
import com.sistema.gestion.Repositories.Admin.Management.VirtualCampus.CourseSubSectionRepository;


import java.util.ArrayList;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Service
public class CourseSubSectionService {

    private final FileService fileService;

    private final CourseSubSectionRepository courseSubSectionRepository;

    public CourseSubSectionService(CourseSubSectionRepository courseSubSectionRepository, FileService fileService) {
        this.courseSubSectionRepository = courseSubSectionRepository;
        this.fileService = fileService;
    }

    public Flux<CourseSubSection> findAll(ServerWebExchange exchange) {
        // ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        // if (template == null) {
        //     return Flux.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        // }
        // return template.findAll(CourseSubSection.class);
        return courseSubSectionRepository.findAll();
    }

    public Mono<CourseSubSection> findById(String id) {
        // ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        // if (template == null) {
        //     return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        // }
        // return template.findById(id, CourseSubSection.class);
        return courseSubSectionRepository.findById(id);
    }

    public Mono<CourseSubSection> findContentByIdSubsection(String id) {
        // ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        // if (template == null) {
        //     return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        // }
        // return template.findById(id, CourseSubSection.class);
        return courseSubSectionRepository.findById(id);
    }

    public Mono<CourseSubSection> create(ServerWebExchange exchange, CourseSubSection courseSubSection) {
        // ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        // if (template == null) {
        //     return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        // }
        // return template.save(courseSubSection);
        return courseSubSectionRepository.save(courseSubSection);
    }

    public Mono<CourseSubSection> update(ServerWebExchange exchange, String id, CourseSubSection courseSubSection) {
        // ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        // if (template == null) {
        //     return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        // }
        // courseSubSection.setId(id);
        // return template.save(courseSubSection);
        return courseSubSectionRepository.updateTitleById(courseSubSection.getId(), courseSubSection.getTitle())
        .then(courseSubSectionRepository.updateBodyById(courseSubSection.getId(), courseSubSection.getBody()));
    }

    public Mono<Void> delete(ServerWebExchange exchange, String id) {
        // ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        // if (template == null) {
        //     return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        // }
        // return template.remove(CourseSubSection.class, id);
        return courseSubSectionRepository.deleteById(id);
    }

    public Mono<String> addFile(ServerWebExchange exchange, String subSectionId, String name, Mono<FilePart> file) {
        // ReactiveMongoTemplate template = (ReactiveMongoTemplate) exchange.getAttribute("mongoTemplate");
        // if (template == null) {
        //     return Mono.error(new IllegalStateException("No se encontró la conexión a la base de datos."));
        // }
        // return fileService.saveFile(exchange, name, file)
        //     .flatMap(fileId -> template.findById(subSectionId, CourseSubSection.class)
        //         .flatMap(subSection -> {
        //             ArrayList<String> filesIds = subSection.getFilesIds() == null ? new ArrayList<>() : subSection.getFilesIds();
        //             filesIds.add(fileId);
        //             subSection.setFilesIds(filesIds);
        //             return template.save(subSection);
        //         })
        //         .map(savedSubSection -> fileId)
        //     );
        return fileService.saveFile(exchange, file)
                .flatMap(fileId -> courseSubSectionRepository.findById(subSectionId)
                        .flatMap(subSection -> {
                            ArrayList<String> filesIds = subSection.getFilesIds() == null ? new ArrayList<>() : subSection.getFilesIds();
                            filesIds.add(fileId);
                            subSection.setFilesIds(filesIds);
                            return courseSubSectionRepository.save(subSection);
                        })
                        .map(savedSubSection -> fileId)
                );
    }
    
}
