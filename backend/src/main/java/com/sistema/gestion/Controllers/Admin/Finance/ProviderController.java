package com.sistema.gestion.Controllers.Admin.Finance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.Models.Admin.Finance.Provider;
import com.sistema.gestion.Services.Admin.Finance.ProviderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/proveedores")
@Tag(name = "Provider Controller", description = "Controlador para la gestión de proveedores")
public class ProviderController {

  @Autowired
  private final ProviderService providerService;

  public ProviderController(ProviderService providerService) {
    this.providerService = providerService;
  }

  @Operation(summary = "Obtener todos los proveedores", description = "Retorna una lista de todos los proveedores registrados")
  @ApiResponse(responseCode = "200", description = "Lista de proveedores obtenida exitosamente")
  @GetMapping
  public Mono<ResponseEntity<Flux<Provider>>> getAllProviders() {
    return providerService.getAllProviders()
        .collectList()
        .map(provider -> ResponseEntity.ok().body(Flux.fromIterable(provider)))
        .defaultIfEmpty(ResponseEntity.noContent().build());
  }

  @Operation(summary = "Obtener proveedor por ID", description = "Retorna la información de un proveedor específico por su ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Proveedor obtenido exitosamente"),
      @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
  })
  @GetMapping("/{providerId}")
  public Mono<ResponseEntity<Provider>> getProviderById(
      @Parameter(description = "ID del proveedor a buscar", required = true) @PathVariable String providerId) {
    return providerService.getProviderById(providerId)
        .map(ResponseEntity::ok)
        .onErrorMap(e -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Error al tratar de obtener el proveedor con el ID: " + providerId))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @Operation(summary = "Registrar un nuevo proveedor", description = "Crea un nuevo proveedor en el sistema")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Proveedor creado exitosamente"),
      @ApiResponse(responseCode = "400", description = "Solicitud inválida")
  })
  @PostMapping
  public Mono<ResponseEntity<Provider>> saveProvider(@RequestBody Provider provider, Authentication authentication) {
    String user = authentication.getName();
    return providerService.saveProvider(provider, user)
        .map(savedProvider -> ResponseEntity.status(HttpStatus.CREATED).body(savedProvider))
        .onErrorMap(e -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Error al registrar el proveedor"));
  }

  @Operation(summary = "Actualizar proveedor por ID", description = "Actualiza los datos de un proveedor existente")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Proveedor actualizado exitosamente"),
      @ApiResponse(responseCode = "400", description = "IDs no coinciden o solicitud inválida"),
      @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
  })
  @PutMapping("/{providerId}")
  public Mono<ResponseEntity<Provider>> updateProvider(@RequestBody Provider provider,
      @PathVariable String providerId, Authentication authentication) {
    String user = authentication.getName();
    return providerService.updateProvider(provider, providerId, user)
        .map(updatedProvider -> ResponseEntity.ok(updatedProvider))
        .onErrorMap(e -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Error al modificar o actualizar el proveedor"));
  }

  @Operation(summary = "Eliminar proveedor por ID", description = "Elimina un proveedor del sistema")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Proveedor eliminado exitosamente"),
      @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
  })
  @DeleteMapping("/{providerId}")
  public Mono<ResponseEntity<Void>> deleteProvider(
      @Parameter(description = "ID del proveedor a eliminar", required = true) @PathVariable String providerId) {
    return providerService.deleteProvider(providerId)
        .then(Mono.just(ResponseEntity.noContent().build()));
  }
}
