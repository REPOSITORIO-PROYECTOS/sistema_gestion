package com.sistema.gestion.Services.Profiles;

import com.sistema.gestion.DTO.PagedResponse;
import com.sistema.gestion.DTO.UserInfo;
import com.sistema.gestion.Models.Profiles.User;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.Repositories.Profiles.UserRepository;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Mono<PagedResponse<UserInfo>> getUsersPaged(int page, int size, String keyword) {
        PageRequest pageRequest = PageRequest.of(page, size);

        if (keyword != null && !keyword.isEmpty()) {
            Mono<Long> totalElementsMono = userRepository.countByKeyword(keyword);
            Flux<UserInfo> userFlux = userRepository.findByKeywordPaged(keyword, pageRequest)
                    .map(user -> {
                        UserInfo userInfo = new UserInfo();
                        userInfo.setUser(user);
                        return userInfo;
                    });

            return Mono.zip(totalElementsMono, userFlux.collectList())
                    .map(tuple -> new PagedResponse<>(
                            tuple.getT2(), // Lista de usuarios filtrados
                            tuple.getT1(), // Total de registros filtrados
                            page,
                            size));
        }

        Mono<Long> totalElementsMono = userRepository.count();
        Flux<UserInfo> userFlux = userRepository.findUsersPaged(pageRequest)
                .map(user -> {
                        UserInfo userInfo = new UserInfo();
                        userInfo.setUser(user);
                        return userInfo;
                });

        return Mono.zip(totalElementsMono, userFlux.collectList())
                .map(tuple -> new PagedResponse<>(
                        tuple.getT2(),
                        tuple.getT1(), // Total de registros
                        page,
                        size));
    }

    public Mono<UserInfo> findById(String id) {
        return userRepository.findById(id)
                .map(user -> {
                        UserInfo userInfo = new UserInfo();
                        userInfo.setUser(user);
                        return userInfo;
                })
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró el usuario con ID: " + id)));
    }

    public Mono<User> getFullName(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró el usuario: " + email)))
                .map(user -> user);
    }

    // Método para crear un usuario
    public Mono<UserInfo> createUser(UserInfo userInfo) {
        // Convierte el UserInfo a la entidad correspondiente y guarda el usuario
        // return userRepository.save(userInfo.toUserEntity())
        //         .map(UserInfo::new);
		return Mono.just(userInfo);
    }

    // Método para editar un usuario
    public Mono<UserInfo> updateUser(String id, UserInfo userInfo) {
        return userRepository.findById(id)
                .flatMap(existingUser -> {
                    // Actualiza los campos necesarios con los nuevos datos
                    existingUser.setName(userInfo.getUser().getName());
                    existingUser.setSurname(userInfo.getUser().getSurname());
                    existingUser.setEmail(userInfo.getUser().getEmail());
                    // Otros campos que puedas necesitar actualizar
                    return userRepository.save(existingUser);
                })
                .map(user -> {
                        UserInfo userInf = new UserInfo();
                        userInf.setUser(user);
                        return userInf;
                })
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró el usuario con ID: " + id)));
    }

    // Método para eliminar un usuario
    public Mono<Void> deleteUser(String id) {
        return userRepository.findById(id)
                .flatMap(existingUser -> userRepository.delete(existingUser))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró el usuario con ID: " + id)));
    }
}
