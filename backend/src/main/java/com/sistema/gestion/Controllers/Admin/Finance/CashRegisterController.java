package com.sistema.gestion.Controllers.Admin.Finance;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.Models.Admin.Finance.CashRegister;
import com.sistema.gestion.Services.Admin.Finance.CashRegisterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/caja")
@Tag(name = "Cash Register", description = "Endpoints para gestionar las caja")
@RequiredArgsConstructor
public class CashRegisterController {

    private final CashRegisterService cashRegisterService;

    @Operation(summary = "Abrir una caja", description = "Crea una nueva caja si no hay ninguna abierta actualmente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Caja abierta correctamente"),
            @ApiResponse(responseCode = "400", description = "Ya existe una caja abierta o error al abrirla")
    })
    @PostMapping("/abrir")
    public Mono<ResponseEntity<CashRegister>> openCashRegister() {
        String user = "ADMIN"; // Se cambia cuando se implemente la seguridad

        return cashRegisterService.openCashRegister(user)
                .map(ResponseEntity::ok)
                .onErrorMap(e -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al abrir la caja.", e));
    }

    @Operation(summary = "Cerrar una caja", description = "Cierra la caja abierta actualmente y calcula el total acumulado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Caja cerrada correctamente"),
            @ApiResponse(responseCode = "400", description = "No hay una caja abierta o error al cerrarla")
    })
    @PostMapping("/cerrar")
    public Mono<ResponseEntity<CashRegister>> closeCashRegister() {
        String user = "ADMIN"; // Se cambia cuando se implemente la seguridad

        return cashRegisterService.closeCashRegister(user)
                .map(ResponseEntity::ok)
                .onErrorMap(e -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al cerrar la caja.", e));
    }

    @Operation(summary = "Consultar caja abierta", description = "Obtiene los detalles de la caja abierta actualmente, incluyendo un cálculo provisional de los pagos realizados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Caja abierta obtenida correctamente"),
            @ApiResponse(responseCode = "404", description = "No hay caja abierta")
    })

    @GetMapping("/estado")
    public Mono<ResponseEntity<CashRegister>> getOpenCashRegister() {
        return cashRegisterService.getOpenCashRegister()
                .map(ResponseEntity::ok)
                .onErrorMap(e -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay caja abierta.", e));
    }
}
