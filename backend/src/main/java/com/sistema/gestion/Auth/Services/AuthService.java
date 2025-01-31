package com.sistema.gestion.Auth.Services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sistema.gestion.Auth.Utils.JwtUtil;
import com.sistema.gestion.DTO.UserCredentialsDTO;
import com.sistema.gestion.Exceptions.UserNotFoundException;
import com.sistema.gestion.Models.Profiles.User;
import com.sistema.gestion.Repositories.Profiles.UserRepository;
import com.sistema.gestion.Services.Profiles.UserService;

import reactor.core.publisher.Mono;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  @Autowired
  private final UserService userService;

  public AuthService(UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      JwtUtil jwtUtil, UserService userService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    this.userService = userService;
  }

  public Mono<User> registerUser(User user, String username) {
    return userService.getFullName(username)
        .flatMap(name -> {
          return userRepository.findByEmail(user.getEmail())
              .flatMap(existingUser -> Mono.<User>error(new RuntimeException("El usuario ya existe")))
              .switchIfEmpty(Mono.defer(() -> {
                user.setCreatedBy(name);
                user.setCreatedAt(LocalDateTime.now());
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.getRoles().add("ROLE_USER");
                return userRepository.save(user);
              }))
              .onErrorMap(e -> new RuntimeException("Error al registrar el usuario: " + e.getMessage()));
        });
  }

  public Mono<User> updateUser(User user, String username, String userId) {
    return userService.getFullName(username)
        .flatMap(name -> {
          return userRepository.findByEmail(user.getEmail())
              .switchIfEmpty(Mono.error(new RuntimeException("El email no ha sido registrado aun.")))
              .flatMap(existingUser -> {
                existingUser.setModifiedBy(name);
                existingUser.setUpdatedAt(LocalDateTime.now());
                existingUser.setName(user.getName());
                existingUser.setSurname(user.getSurname());
                existingUser.setPhone(user.getPhone());
                existingUser.setEmail(user.getEmail());
                existingUser.setDni(user.getDni());
                existingUser.setRoles(user.getRoles());
                if (user.getPassword() != null && !user.getPassword().isEmpty()
                    && !user.getPassword().isBlank()) {
                  existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                }
                return userRepository.save(existingUser);
              });
        })
        .onErrorMap(e -> new RuntimeException("Error al registrar el usuario: " + e.getMessage()));
  }

  public Mono<UserCredentialsDTO> login(String username, String password) {
    return userRepository.findByEmail(username)
        .switchIfEmpty(Mono.error(new UserNotFoundException("Usuario no encontrado")))
        .flatMap(user -> {
          if (passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtil.generateToken(user.getEmail(),
                user.getRoles().toArray(new String[0]));
            return userService.getFullName(username)
                .flatMap(name -> {
                  UserCredentialsDTO credentialsDTO = new UserCredentialsDTO();
                  credentialsDTO.setToken(token);
                  credentialsDTO.setName(name);
                  credentialsDTO.setUsername(username);
                  credentialsDTO.setRole(user.getRoles());
                  return Mono.just(credentialsDTO);
                });
          } else {
            return Mono.error(new RuntimeException("Credenciales incorrectas"));
          }
        });
  }
}
