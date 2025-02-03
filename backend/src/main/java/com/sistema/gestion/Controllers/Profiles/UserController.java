package com.sistema.gestion.Controllers.Profiles;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.sistema.gestion.Models.Profiles.User;
import com.sistema.gestion.Services.Profiles.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Users Controller", description = "Controlador para la gestión de usuarios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    String username = "Pepe Hongo - admin";

    @GetMapping("/todos")
    public Flux<User> findAll(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return userService.findAll(page, size);
    }

    @GetMapping("/{id}")
    public Mono<User> findById(@PathVariable String id) {
        return userService.findById(id);
    }

    @PostMapping("/crear")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> createUser(@RequestBody @Valid User user) {
        System.out.println(user);
        return userService.createUser(user, username);
    }

    @PutMapping("/actualizar/{id}")
    public Mono<User> actualizarUsuario(@PathVariable String id, @RequestBody @Valid User user) {
        return userService.updateUser(id, user, username);
    }

    @DeleteMapping("/eliminar/{id}")
    public Mono<Void> eliminarUsuario(@PathVariable String id) {
        return userService.deleteUser(id);
    }
}