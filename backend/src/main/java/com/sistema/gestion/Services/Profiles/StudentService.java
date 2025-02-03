package com.sistema.gestion.Services.Profiles;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.sistema.gestion.Models.Profiles.Student;
import com.sistema.gestion.Repositories.Profiles.StudentRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public Mono<Student> createStudent(Student student, String user) {
        student.setCreatedBy(user);
        return studentRepository.save(student);
    }

    public Flux<Student> findAll(int page, int size) {
        return studentRepository.findAll()
            .sort((student1, student2) -> student1.getSurname().compareTo(student2.getSurname()))
            .skip((long) page * size)
            .take(size);
    }

    public Mono<Long> findAllCount() {
        return studentRepository.count();
    }

    public Mono<Student> findById(String id) {
        return studentRepository.findById(id);
    }

    public Mono<Student> updateStudent(String id, Student student, String user) {
        return studentRepository.findById(id)
                .flatMap(existingStudent -> {
                    existingStudent.setName(student.getName());
                    existingStudent.setSurname(student.getSurname());
                    existingStudent.setUpdatedAt(LocalDateTime.now());
                    existingStudent.setModifiedBy(user);
                    existingStudent.setEmail(student.getEmail());
                    existingStudent.setPhone(student.getPhone());
                    return studentRepository.save(existingStudent);
                });
    }

    public Mono<Void> deleteStudent(String id) {
        return studentRepository.deleteById(id);
    }
}

