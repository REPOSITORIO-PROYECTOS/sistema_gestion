package com.sistema.gestion.Auth.Controllers;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.Auth.Services.AuthService;
import com.sistema.gestion.DTO.LoginRequest;
import com.sistema.gestion.DTO.UserCredentialsDTO;
import com.sistema.gestion.Exceptions.UserNotFoundException;
import com.sistema.gestion.Models.Profiles.User;
import com.sistema.gestion.Repositories.Profiles.UserRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthService authService;

	// TODO: refactorizar, generar errores, generar DTO sin password desde consulta
	// mongo
	@GetMapping
	public Mono<ResponseEntity<Flux<User>>> getAllUsers() {
		return userRepository.findAll()
				.collectList()
				.map(course -> ResponseEntity.ok().body(Flux.fromIterable(course)))
				.defaultIfEmpty(ResponseEntity.noContent().build());
	}

	@GetMapping("/{userId}")
	public Mono<ResponseEntity<User>> getUserById(@PathVariable String userId) {
		return userRepository.findById(userId)
				.map(ResponseEntity::ok)
				.onErrorMap(e -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Error al tratar de obtener el usuario con el ID: " + userId))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	// TODO end

	@PostMapping("/registrar")
	public Mono<User> registerUser(@RequestBody User user, Authentication auth) {
		String username = auth.getName();
		return authService.registerUser(user, username)
				.onErrorMap(e -> new RuntimeException("Error al registrar el usuario"));
	}

	@PutMapping("/editar/{userId}")
	public Mono<User> updateUser(@RequestBody User user, @PathVariable String userId, Authentication auth) {
		String username = auth.getName();
		return authService.updateUser(user, username, userId)
				.onErrorMap(e -> new RuntimeException("Error al actualizar el usuario"));
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

	@PostMapping("/login")
	public Mono<ResponseEntity<UserCredentialsDTO>> login(@RequestBody LoginRequest loginRequest) {
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

					System.out.println("TOKEN COOKIE: " + tokenCookie.toString());

					// Retornar el ResponseEntity con el body
					return Mono.just(ResponseEntity.status(HttpStatus.ACCEPTED)
							.header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
							.body(credentialsDTO));
				})
				.onErrorResume(UserNotFoundException.class,
						ex -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()))
				.onErrorResume(RuntimeException.class, ex -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
	}

}