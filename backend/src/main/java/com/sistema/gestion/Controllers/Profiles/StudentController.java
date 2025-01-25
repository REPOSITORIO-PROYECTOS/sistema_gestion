package com.sistema.gestion.Controllers.Profiles;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.sistema.gestion.Models.Profiles.Student;
import com.sistema.gestion.Services.Profiles.StudentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/estudiantes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    String user = "Pepe Hongo - admin";

    @GetMapping("/todos")
    public Flux<Student> findAll(@RequestParam(defaultValue = "0") int page, 
    @RequestParam(defaultValue = "5") int size) {
        return studentService.findAll(page, size);
    }

    @GetMapping("/{id}")
    public Mono<Student> findById(@PathVariable String id) {
        return studentService.findById(id);
    }

    @PostMapping("/crear")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Student> createStudent(@RequestBody @Valid Student student) {
        return studentService.createStudent(student, user);
    }

    @PutMapping("/actualizar/{id}")
    public Mono<Student> actualizarUsuario(@PathVariable String id, @RequestBody @Valid Student student) {
        return studentService.updateStudent(id, student, user);
    }

    @DeleteMapping("/eliminar/{id}")
    public Mono<Void> eliminarUsuario(@PathVariable String id) {
        return studentService.deleteStudent(id);
    }
}
