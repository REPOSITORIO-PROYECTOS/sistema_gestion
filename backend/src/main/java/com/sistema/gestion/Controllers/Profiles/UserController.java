package com.sistema.gestion.Controllers.Profiles;

import com.sistema.gestion.DTO.PagedResponse;
import com.sistema.gestion.DTO.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sistema.gestion.Services.Profiles.UserService;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Controlador para la gestión de usuarios")
@CrossOrigin(origins = "*")
public class UserController {

	private final UserService userService;

	@Operation(summary = "Obtener usuarios paginados", description = "Devuelve una lista paginada de usuarios.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente."),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.")
	})
	@GetMapping("/paged")
	public Mono<PagedResponse<UserInfo>> getUsersPaged(
			@Parameter(description = "Número de página (por defecto 0)", example = "0") @RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Tamaño de la página (por defecto 10)", example = "10") @RequestParam(defaultValue = "10") int size,
			@Parameter(description = "Palabra clave para filtrar", example = "Juan") @RequestParam(required = false) String keyword) {
		return userService.getUsersPaged(page, size, keyword);
	}

	

	@Operation(summary = "Obtener usuario por ID", description = "Devuelve un usuario específico por su ID.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Usuario obtenido exitosamente."),
			@ApiResponse(responseCode = "404", description = "Usuario no encontrado."),
			@ApiResponse(responseCode = "400", description = "Error en la solicitud."),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor.")
	})
	@GetMapping("/{userId}")
	public Mono<ResponseEntity<UserInfo>> getUserById(
			@Parameter(description = "ID del usuario", required = true, example = "12345") @PathVariable String userId) {
		return userService.findById(userId)
				.map(ResponseEntity::ok)
				.onErrorMap(e -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Error al tratar de obtener el usuario con el ID: " + userId))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
}