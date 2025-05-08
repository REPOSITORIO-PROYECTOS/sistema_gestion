package com.sistema.gestion.Auth.Controllers;

 import io.swagger.v3.oas.annotations.responses.ApiResponse;
 import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.sistema.gestion.Auth.Services.AuthService;
import com.sistema.gestion.DTO.LoginRequest;
import com.sistema.gestion.DTO.UserCredentialsDTO;
import com.sistema.gestion.DTO.UserInfo;
import com.sistema.gestion.Exceptions.UserNotFoundException;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;

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
    public Mono<UserInfo> registerUser(
            @RequestParam String userType,
            @RequestBody UserInfo userDetails,
            Authentication auth) {
        String username = auth.getName();
        switch (userType.toLowerCase()) {
            case "student":
                return authService.registerStudent(userDetails, username).map(user -> {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setStudent(user);
                    return userInfo;
                });  // Registrar estudiante
            case "teacher":
                return authService.registerTeacher(userDetails, username).map(user -> {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setTeacher(user);
                    return userInfo;
                });  // Registrar profesor
            case "user":
                return authService.registerUser(userDetails, username).map(user -> {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUser(user);
                    return userInfo;
                });
            case "parent":
                return authService.registerParent(userDetails, username).map(user -> {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setParent(user);
                    return userInfo;
                });
            default:
                return Mono.empty();
                  // Registrar usuario
        }
    }

    @Operation(summary = "Actualizar un usuario", description = "Actualiza la información de un usuario existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/editar/{userId}")
    public Mono<UserInfo> updateUser(
            @RequestParam String userType,
            @RequestBody UserInfo userDetails,
            @PathVariable String userId,
            Authentication auth) {
        String username = auth.getName();

        switch (userType.toLowerCase()) {
            case "student":
                return authService.updateStudent(userDetails, username, userId).map(user -> {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setStudent(user);
                    return userInfo;
                });  // Actualizar estudiante
            case "teacher":
                return authService.updateTeacher(userDetails, username, userId).map(user -> {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setTeacher(user);
                    return userInfo;
                });   // Actualizar profesor
            case "user":
                return authService.updateUser(userDetails, username, userId).map(user -> {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUser(user);
                    return userInfo;
                });   // Actualizar usuario
            case "parent":
                return authService.updateParent(userDetails, username, username).map(user -> {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setParent(user);
                    return userInfo;
                });
            default:
                return Mono.empty();   // Actualizar usuario
        }
    }

    @PutMapping("/editar")
    public Mono<UserInfo> updateUserByEmail(
            @RequestParam String userType,
            @RequestBody UserInfo userDetails,
            Authentication auth) {
        String username = auth.getName();

        switch (userType.toLowerCase()) {
            case "student":
                return authService.updateStudentProfile(userDetails, username).map(user -> {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setStudent(user);
                    return userInfo;
                });  // Actualizar estudiante
            case "teacher":
                return authService.updateTeacherProfile(userDetails, username).map(user -> {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setTeacher(user);
                    return userInfo;
                });   // Actualizar profesor
            case "user":
                return authService.updateUserProfile(userDetails, username).map(user -> {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUser(user);
                    return userInfo;
                });   // Actualizar usuario
            case "parent":
                return authService.updateParentProfile(userDetails, username).map(user -> {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setParent(user);
                    return userInfo;
                });
            default:
                return Mono.empty();   // Actualizar usuario
        }
    }

    @Operation(summary = "Eliminar usuario por ID", description = "Elimina un usuario del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/eliminar/{userId}")
    public Mono<ResponseEntity<Void>> deleteUser(
            @RequestParam String userType,
            @PathVariable String userId) {
        switch (userType.toLowerCase()) {
            case "student":
                return authService.deleteStudent(userId).then(Mono.just(ResponseEntity.noContent().build()));  // Eliminar estudiante
            case "parent":
                return authService.deleteParent(userId).then(Mono.just(ResponseEntity.noContent().build()));  // Eliminar usuario
            case "teacher":
                return authService.deleteTeacher(userId).then(Mono.just(ResponseEntity.noContent().build()));  // Eliminar profesor
            case "user":
            default:
                return authService.deleteUser(userId).then(Mono.just(ResponseEntity.noContent().build()));  // Eliminar usuario
        }
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
            @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest.getDni(), loginRequest.getPassword())
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
}