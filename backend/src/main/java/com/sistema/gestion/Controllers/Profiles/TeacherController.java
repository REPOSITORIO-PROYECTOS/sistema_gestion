package com.sistema.gestion.Controllers.Profiles;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sistema.gestion.Models.Profiles.Teacher;
import com.sistema.gestion.Services.Profiles.TeacherService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/profesores")
@Tag(name = "Teacher Controller", description = "Controlador para la gestión de profesores")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping("/todos")
    public Flux<Teacher> todos(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return teacherService.findAll(page, size);
    }

    @GetMapping("/contar-todos")
    public Mono<Long> contarTodos() {
        return teacherService.findAllCount();
    }

    @GetMapping("/{id}")
    public Mono<Teacher> findById(@PathVariable String id) {
        return teacherService.findById(id);
    }

    @PostMapping("/crear")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Teacher> crear(@RequestBody @Valid Teacher teacher) {
        String user = "Pepe Hongo - admin";
        return teacherService.create(teacher, user);
    }

    @PutMapping("/actualizar/{id}")
    public Mono<Teacher> actualizar(@PathVariable String id, @RequestBody @Valid Teacher teacher) {
        String user = "Pepe Hongo - admin";
        return teacherService.update(id, teacher, user);
    }

    @DeleteMapping("/eliminar/{id}")
    public Mono<Void> eliminar(@PathVariable String id) {
        return teacherService.delete(id);
    }
}
