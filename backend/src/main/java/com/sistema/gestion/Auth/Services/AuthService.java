package com.sistema.gestion.Auth.Services;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sistema.gestion.Auth.Utils.JwtUtil;
import com.sistema.gestion.DTO.UserCredentialsDTO;
import com.sistema.gestion.Exceptions.UserNotFoundException;
import com.sistema.gestion.Models.Profiles.User;
import com.sistema.gestion.Repositories.Profiles.UserRepository;
import com.sistema.gestion.Services.Profiles.UserService;

import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final UserService userService;

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
								existingUser.setName(user.getName() != null ? user.getName() : existingUser.getName());
								existingUser.setSurname(user.getSurname() != null ? user.getSurname() : existingUser.getSurname());
								existingUser.setPhone(user.getPhone() != null ? user.getPhone() : existingUser.getPhone());
								existingUser.setEmail(user.getEmail() != null ? user.getEmail() : existingUser.getEmail());
								existingUser.setDni(user.getDni() != null ? user.getDni() : existingUser.getDni());
								existingUser.setRoles(user.getRoles() != null ? user.getRoles() : existingUser.getRoles());
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

	public Mono<Void> deleteUser(String userId) {
		return userRepository.findById(userId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
						"No se encontró el usuario para eliminar con ID: " + userId)))
				.flatMap(userRepository::delete);
	}
}


/*
 public class AuthService {
    
    // Métodos para registrar diferentes tipos de usuarios
    public Mono<User> registerUser(Map<String, Object> userDetails, String username) {
        // Lógica para registrar un usuario general
    }
    
    public Mono<Student> registerStudent(Map<String, Object> userDetails, String username) {
        // Lógica para registrar un estudiante
    }

    public Mono<Teacher> registerTeacher(Map<String, Object> userDetails, String username) {
        // Lógica para registrar un profesor
    }

    // Métodos para actualizar diferentes tipos de usuarios
    public Mono<User> updateUser(Map<String, Object> userDetails, String username, String userId) {
        // Lógica para actualizar un usuario
    }

    public Mono<Student> updateStudent(Map<String, Object> userDetails, String username, String userId) {
        // Lógica para actualizar un estudiante
    }

    public Mono<Teacher> updateTeacher(Map<String, Object> userDetails, String username, String userId) {
        // Lógica para actualizar un profesor
    }

    // Métodos para eliminar diferentes tipos de usuarios
    public Mono<Void> deleteUser(String userId) {
        // Lógica para eliminar un usuario
    }

    public Mono<Void> deleteStudent(String userId) {
        // Lógica para eliminar un estudiante
    }

    public Mono<Void> deleteTeacher(String userId) {
        // Lógica para eliminar un profesor
    }
}

 */