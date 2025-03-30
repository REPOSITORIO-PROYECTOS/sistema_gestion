package com.sistema.gestion.Auth.Controllers;

import java.time.Duration;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.sistema.gestion.Auth.Services.AuthService;
import com.sistema.gestion.DTO.LoginRequest;
import com.sistema.gestion.DTO.UserCredentialsDTO;
import com.sistema.gestion.Exceptions.UserNotFoundException;
import com.sistema.gestion.Models.Profiles.User;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Controller", description = "Controlador para gestionar la autenticación y registro de usuarios")
public class AuthController {

	private final AuthService authService;

	@Operation(summary = "Registrar un nuevo usuario", description = "Registra un nuevo usuario en el sistema")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
			@ApiResponse(responseCode = "400", description = "Error en la solicitud"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@PostMapping("/registrar")
	public Mono<User> registerUser(
			@Parameter(description = "Datos del usuario a registrar", required = true) @RequestBody User user) {
		String username = "ROLE_ADMIN";//auth.getName();
		System.out.println("---------------- REGISTER USER AUTH CONTROLLER ----------------");
		return authService.registerUser(user, username)
				.onErrorMap(e -> new RuntimeException("Error al registrar el usuario"));
	}

	@Operation(summary = "Actualizar un usuario", description = "Actualiza la información de un usuario existente")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
			@ApiResponse(responseCode = "400", description = "Error en la solicitud"),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@PutMapping("/editar/{userId}")
	public Mono<User> updateUser(
			@Parameter(description = "Datos del usuario a actualizar", required = true) @RequestBody User user,
			@Parameter(description = "ID del usuario a actualizar", required = true) @PathVariable String userId,
			Authentication auth) {
		String username = auth.getName();
		return authService.updateUser(user, username, userId)
				.onErrorMap(e -> new RuntimeException("Error al actualizar el usuario"));
	}

	@Operation(summary = "Eliminar usuario por ID", description = "Elimina un usuario del sistema")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@DeleteMapping("/{userId}")
	public Mono<ResponseEntity<Void>> deleteUser(
			@Parameter(description = "ID del usuario a eliminar", required = true) @PathVariable String userId) {
		return authService.deleteUser(userId)
				.then(Mono.just(ResponseEntity.noContent().build()));
	}

	@Operation(summary = "Iniciar sesión", description = "Inicia sesión en el sistema y devuelve un token JWT")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Inicio de sesión exitoso"),
			@ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor")
	})
	@PostMapping("/login")
	public Mono<ResponseEntity<UserCredentialsDTO>> login(
			@Parameter(description = "Credenciales de inicio de sesión", required = true) @RequestBody LoginRequest loginRequest) {
		return authService.login(loginRequest.getUsername(), loginRequest.getPassword())
				.flatMap(credentialsDTO -> {
					// Crear la cookie con el token JWT
					ResponseCookie tokenCookie = ResponseCookie.from("token", credentialsDTO.getToken())
							.httpOnly(true)
							.secure(true)
							.path("/")
							.maxAge(Duration.ofHours(8))
							.sameSite("Strict")
							.build();

					// Retornar el ResponseEntity con el body
					return Mono.just(ResponseEntity.status(HttpStatus.ACCEPTED)
							.header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
							.body(credentialsDTO));
				})
				.onErrorResume(UserNotFoundException.class,
						ex -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()))
				.onErrorResume(RuntimeException.class, ex -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
	}


	/*
	 * @PostMapping("/login")
	 * public Mono<ResponseEntity<UserCredentialsDTO>> login(@RequestBody
	 * LoginRequest loginRequest) {
	 * return authService.login(loginRequest.getUsername(),
	 * loginRequest.getPassword())
	 * .map(credentialsDTO -> {
	 * return ResponseEntity.status(HttpStatus.ACCEPTED).body(credentialsDTO);
	 * })
	 * .onErrorMap(
	 * e -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
	 * "Error al iniciar sesión. " + e.getMessage()));
	 * }
	 */
}