package com.sistema.gestion.Services.Profiles;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.sistema.gestion.Models.Profiles.Teacher;
import com.sistema.gestion.Repositories.Profiles.TeacherRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private TeacherRepository teacherRepository;

    public Mono<Teacher> create(Teacher teacher, String user) {
        teacher.setCreatedBy(user);
        return teacherRepository.save(teacher);
    }

    public Flux<Teacher> findAll(int page, int size) {
        return teacherRepository.findAll()
            .sort((teacher1, teacher2) -> teacher1.getSurname().compareTo(teacher2.getSurname()))
            .skip((long) page * size)
            .take(size);
    }

    public Mono<Teacher> findById(String id) {
        return teacherRepository.findById(id);
    }

    public Mono<Teacher> update(String id, Teacher teacher, String user) {
        return teacherRepository.findById(id)
                .flatMap(existingTeacher -> {
                    existingTeacher.setName(teacher.getName());
                    existingTeacher.setSurname(teacher.getSurname());
                    existingTeacher.setUpdatedAt(LocalDateTime.now());
                    existingTeacher.setModifiedBy(user);
                    existingTeacher.setEmail(teacher.getEmail());
                    existingTeacher.setPhone(teacher.getPhone());
                    return teacherRepository.save(existingTeacher);
                });
    }

    public Mono<Void> delete(String id) {
        return teacherRepository.deleteById(id);
    }
}
