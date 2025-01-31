package com.sistema.gestion.Controllers.Profiles;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.Models.Profiles.Student;
import com.sistema.gestion.Services.Profiles.StudentService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/estudiantes")
public class StudentController {

  private final StudentService studentService;

  public StudentController(StudentService studentService) {
    this.studentService = studentService;
  }

  @GetMapping("/todos")
  public Flux<Student> findAll(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size) {
    return studentService.findAll(page, size)
        .onErrorResume(e -> Mono.error(new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener la lista de estudiantes.")));
  }

  @GetMapping("/{id}")
  public Mono<Student> findById(@PathVariable String id) {
    return studentService.findById(id)
        .switchIfEmpty(Mono.error(new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Estudiante no encontrado con ID: " + id)))
        .onErrorResume(e -> Mono.error(new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, "Error al buscar estudiante")));
  }

  @PostMapping("/crear")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Student> createStudent(@RequestBody @Valid Student student, Authentication authentication) {
    String user = authentication.getName();
    return studentService.createStudent(student, user)
        .onErrorResume(e -> Mono.error(new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Error al crear estudiante")));
  }

  @PutMapping("/actualizar/{id}")
  public Mono<Student> actualizarUsuario(@PathVariable String id, @RequestBody @Valid Student student,
      Authentication authentication) {
    String user = authentication.getName();
    return studentService.updateStudent(id, student, user)
        .switchIfEmpty(Mono.error(new ResponseStatusException(
            HttpStatus.NOT_FOUND, "No se pudo actualizar. Estudiante no encontrado con ID: " + id)))
        .onErrorResume(e -> Mono.error(new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar estudiante")));
  }

  @DeleteMapping("/eliminar/{id}")
  public Mono<Void> eliminarUsuario(@PathVariable String id) {
    return studentService.deleteStudent(id)
        .switchIfEmpty(Mono.error(new ResponseStatusException(
            HttpStatus.NOT_FOUND, "No se pudo eliminar. Estudiante no encontrado con ID: " + id)))
        .onErrorResume(e -> Mono.error(new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar estudiante.")));
  }
}
