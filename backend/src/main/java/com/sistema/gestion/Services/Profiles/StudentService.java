package com.sistema.gestion.Services.Profiles;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.Models.Profiles.Student;
import com.sistema.gestion.Repositories.Profiles.StudentRepository;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Service
public class StudentService {

  @Autowired
  private final StudentRepository studentRepository;

  @Autowired
  private final UserService userService;

  public StudentService(StudentRepository studentRepository, UserService userService) {
    this.studentRepository = studentRepository;
    this.userService = userService;
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

  public Mono<Student> createStudent(Student student, String user) {
    if (student.getId() != null && !student.getId().isEmpty()) {
      return Mono.error(
          new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estudiante ya tiene un ID resgistrado,"
              + " no se puede almacenar un nuevo estudiante con ID ya registrado"));
    }
    return userService.getFullName(user)
        .flatMap(name -> {
          student.setCreatedBy(name);
          student.setCreatedAt(LocalDateTime.now());
          return studentRepository.save(student);
        });
  }

  public Mono<Student> updateStudent(String id, Student student, String user) {
    return userService.getFullName(user)
        .flatMap(name -> {
          return studentRepository.findById(id)
              .flatMap(existingStudent -> {
                if (existingStudent.getId().equals(id)) {
                  existingStudent.setName(student.getName());
                  existingStudent.setSurname(student.getSurname());
                  existingStudent.setUpdatedAt(LocalDateTime.now());
                  existingStudent.setModifiedBy(name);
                  existingStudent.setEmail(student.getEmail());
                  existingStudent.setPhone(student.getPhone());
                  return studentRepository.save(existingStudent);
                }
                return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "El estudiante no existe"));
              });
        });
  }

  public Mono<Void> deleteStudent(String id) {
    return studentRepository.deleteById(id);
  }
}
