package com.sistema.gestion.Services.Admin.Management.VirtualCampus;

import com.sistema.gestion.Models.Admin.Management.VirtualCampus.CourseSubSection;
import com.sistema.gestion.Repositories.Admin.Management.VirtualCampus.CourseSubSectionRepository;

import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public Mono<String> addFile(String subSectionId, String name, MultipartFile file) {
        return fileService.saveFile(name, file)
            .flatMap(fileId -> this.findById(subSectionId)
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
