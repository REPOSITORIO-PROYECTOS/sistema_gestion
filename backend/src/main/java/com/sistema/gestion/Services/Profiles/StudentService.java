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

    private StudentRepository studentRepository;

    public Mono<Student> createStudent(Student student) {
        return studentRepository.save(student);
    }

    public Flux<Student> findAll(int page, int size) {
        return studentRepository.findAll()
            .sort((student1, student2) -> student1.getSurname().compareTo(student2.getSurname()))
            .skip((long) page * size)
            .take(size);
    }

    public Mono<Student> findById(String id) {
        return studentRepository.findById(id);
    }

    public Mono<Student> updateStudent(String id, Student student) {
        return studentRepository.findById(id)
                .flatMap(existingStudent -> {
                    existingStudent.setName(student.getName());
                    existingStudent.setSurname(student.getSurname());
                    existingStudent.setUpdatedAt(LocalDateTime.now());
                    existingStudent.setModifiedBy(null); //TODO: Colocar nombre del usuario que realizo la modificacion
                    existingStudent.setEmail(student.getEmail());
                    existingStudent.setPhone(student.getPhone());
                    return studentRepository.save(existingStudent);
                });
    }

    public Mono<Void> deleteStudent(String id) {
        return studentRepository.deleteById(id);
    }
}

