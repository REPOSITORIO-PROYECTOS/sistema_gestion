package com.sistema.gestion.Services.Profiles;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.DTO.PagedResponse;
import com.sistema.gestion.Models.Profiles.Student;
import com.sistema.gestion.Repositories.Profiles.StudentRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class StudentService {

  private final StudentRepository studentRepository;

  public Mono<PagedResponse<Student>> findAll(int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size);

    Mono<Long> totalElementsMono = studentRepository.countAll();
    Flux<Student> studentsFlux = studentRepository.findAllBy(pageRequest);

    return Mono.zip(totalElementsMono, studentsFlux.collectList())
        .map(tuple -> new PagedResponse<>(
            tuple.getT2(), // Lista de estudiantes
            tuple.getT1(), // Total de registros
            page,
            size));
  }

  public Mono<PagedResponse<Student>> searchStudents(String query, int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size);

    // Contar total de coincidencias en DNI o Apellido
    Mono<Long> totalElementsMono = studentRepository.countByDniOrSurname(query);
    // Obtener lista de coincidencias con paginación
    Flux<Student> studentsFlux = studentRepository.findByDniOrSurname(query, pageRequest);

    return Mono.zip(totalElementsMono, studentsFlux.collectList())
        .map(tuple -> new PagedResponse<>(
            tuple.getT2(), // Lista de estudiantes encontrados
            tuple.getT1(), // Total de registros encontrados
            page,
            size));
  }

  public Mono<Long> findAllCount() {
    return studentRepository.count();
  }

  public Mono<Student> createStudent(Student student, String user) {
    student.setCreatedBy(user);
    return studentRepository.save(student)
        .onErrorMap(
            e -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar el estudiante", e));
  }

  public Mono<Student> findById(String id) {
    return studentRepository.findById(id)
        .switchIfEmpty(
            Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el estudiante con ID: " + id)))
        .onErrorMap(
            e -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al buscar el estudiante", e));
  }

  public Mono<Student> updateStudent(String id, Student student, String user) {
    return studentRepository.findById(id)
        .switchIfEmpty(
            Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el estudiante con ID: " + id)))
        .flatMap(existingStudent -> {
          existingStudent.setName(student.getName());
          existingStudent.setSurname(student.getSurname());
          existingStudent.setUpdatedAt(LocalDateTime.now());
          existingStudent.setModifiedBy(user);
          existingStudent.setEmail(student.getEmail());
          existingStudent.setPhone(student.getPhone());
          return studentRepository.save(existingStudent)
              .onErrorMap(e -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                  "Error al actualizar el estudiante", e));
        });
  }

  public Mono<Void> deleteStudent(String id) {
    return studentRepository.findById(id)
        .switchIfEmpty(
            Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el estudiante con ID: " + id)))
        .flatMap(student -> studentRepository.deleteById(id)
            .onErrorMap(e -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error al eliminar el estudiante", e)));
  }
}
