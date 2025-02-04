package com.sistema.gestion.Services.Profiles;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.DTO.PagedResponse;
import com.sistema.gestion.Models.Profiles.Teacher;
import com.sistema.gestion.Repositories.Profiles.TeacherRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TeacherService {

  private final TeacherRepository teacherRepository;

  public Mono<PagedResponse<Teacher>> findAll(int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size);

    Mono<Long> totalElementsMono = teacherRepository.countAll()
        .onErrorResume(e -> Mono.error(new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, "Error al contar los docentes", e)));

    Flux<Teacher> teachersFlux = teacherRepository.findAllBy(pageRequest)
        .onErrorResume(e -> Flux.error(new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener la lista de docentes", e)));

    return Mono.zip(totalElementsMono, teachersFlux.collectList())
        .map(tuple -> new PagedResponse<>(
            tuple.getT2(), // Lista de docentes
            tuple.getT1(), // Total de registros
            page,
            size))
        .onErrorResume(e -> Mono.error(new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, "Error al procesar la paginación de docentes", e)));
  }

  public Mono<PagedResponse<Teacher>> searchTeachers(String query, int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size);

    // Contar total de coincidencias en DNI o Apellido
    Mono<Long> totalElementsMono = teacherRepository.countByDniOrSurname(query);
    // Obtener lista de coincidencias con paginación
    Flux<Teacher> teacherFlux = teacherRepository.findByDniOrSurname(query, pageRequest);

    return Mono.zip(totalElementsMono, teacherFlux.collectList())
        .map(tuple -> new PagedResponse<>(
            tuple.getT2(), // Lista de estudiantes encontrados
            tuple.getT1(), // Total de registros encontrados
            page,
            size));
  }

  public Mono<Teacher> findById(String id) {
    return teacherRepository.findById(id)
        .switchIfEmpty(Mono.error(new ResponseStatusException(
            HttpStatus.NOT_FOUND, "No se encontró el docente con ID: " + id)))
        .onErrorResume(e -> Mono.error(new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, "Error al buscar el docente", e)));
  }

  public Mono<Teacher> create(Teacher teacher, String user) {
    teacher.setCreatedBy(user);
    return teacherRepository.save(teacher)
        .onErrorResume(e -> Mono.error(new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, "Error al crear el docente", e)));
  }

  public Mono<Teacher> update(String id, Teacher teacher, String user) {
    return teacherRepository.findById(id)
        .switchIfEmpty(Mono.error(new ResponseStatusException(
            HttpStatus.NOT_FOUND, "No se encontró el docente con ID: " + id)))
        .flatMap(existingTeacher -> {
          existingTeacher.setName(teacher.getName());
          existingTeacher.setSurname(teacher.getSurname());
          existingTeacher.setUpdatedAt(LocalDateTime.now());
          existingTeacher.setModifiedBy(user);
          existingTeacher.setEmail(teacher.getEmail());
          existingTeacher.setPhone(teacher.getPhone());
          return teacherRepository.save(existingTeacher);
        })
        .onErrorResume(e -> Mono.error(new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar el docente", e)));
  }

  public Mono<Void> delete(String id) {
    return teacherRepository.findById(id)
        .switchIfEmpty(Mono.error(new ResponseStatusException(
            HttpStatus.NOT_FOUND, "No se encontró el docente con ID: " + id)))
        .flatMap(existingTeacher -> teacherRepository.deleteById(id))
        .onErrorResume(e -> Mono.error(new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar el docente", e)));
  }
}
