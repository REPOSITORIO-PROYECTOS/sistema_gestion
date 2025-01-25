package com.sistema.gestion.Services.Profiles;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.sistema.gestion.Models.Profiles.User;
import com.sistema.gestion.Repositories.Profiles.UserRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Mono<User> createUser(User user, String createdBy) {
        user.setCreatedBy(createdBy);
        return userRepository.save(user);
    }

    public Flux<User> findAll(int page, int size) {
        return userRepository.findAll()
            .sort((user1, user2) -> user1.getSurname().compareTo(user2.getSurname()))
            .skip((long) page * size)
            .take(size);
    }

    public Mono<User> findById(String id) {
        return userRepository.findById(id);
    }

    public Mono<User> updateUser(String id, User user, String updatedBy) {
        return userRepository.findById(id)
                .flatMap(existingUsuario -> {
                    existingUsuario.setName(user.getName());
                    existingUsuario.setSurname(user.getSurname());
                    existingUsuario.setUpdatedAt(LocalDateTime.now());
                    existingUsuario.setModifiedBy(updatedBy); //TODO: Colocar nombre del usuario que realizo la modificacion
                    existingUsuario.setEmail(user.getEmail());
                    existingUsuario.setPhone(user.getPhone());
                    return userRepository.save(existingUsuario);
                });
    }

    public Mono<Void> deleteUser(String id) {
        return userRepository.deleteById(id);
    }
}

