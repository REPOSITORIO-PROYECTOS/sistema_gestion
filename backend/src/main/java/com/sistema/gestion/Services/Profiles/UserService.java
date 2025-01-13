package com.sistema.gestion.Services.Profiles;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistema.gestion.Models.Profiles.User;
import com.sistema.gestion.Repositories.Profiles.UserRepository;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Crear un nuevo usuario
    public Mono<User> createUser(User user) {
        return userRepository.save(user);
    }

    // Obtener todos los usuarios
    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    // Obtener un usuario por ID
    public Mono<User> findById(String id) {
        return userRepository.findById(id);
    }

    // Actualizar un usuario
    public Mono<User> updateUser(String id, User user) {
        return userRepository.findById(id)
                .flatMap(existingUsuario -> {
                    existingUsuario.setName(user.getName());
                    existingUsuario.setSurname(user.getSurname());
                    existingUsuario.setUpdatedAt(LocalDateTime.now());
                    existingUsuario.setModifiedBy(null); //TODO: Colocar nombre del usuario que realizo la modificacion
                    existingUsuario.setEmail(user.getEmail());
                    existingUsuario.setPhone(user.getPhone());
                    return userRepository.save(existingUsuario);
                });
    }

    // Eliminar un usuario
    public Mono<Void> deleteUser(String id) {
        return userRepository.deleteById(id);
    }
}

