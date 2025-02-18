package com.sistema.gestion.Controllers.Admin.Finance;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.DTO.PagedResponse;
import com.sistema.gestion.DTO.PaymentWithStudentDTO;
import com.sistema.gestion.Models.Admin.Finance.Payment;
import com.sistema.gestion.Services.Admin.Finance.PaymentService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/pagos")
@Tag(name = "Pagos", description = "Operaciones relacionadas con los pagos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PaymentController {

  private static final String DEFAULT_USER = "ADMIN"; // Temporal hasta implementar seguridad

  private final PaymentService paymentService;

  @Operation(summary = "Obtener pagos paginados", description = "Devuelve una lista paginada de pagos.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida exitosamente.")
  })
  @GetMapping("/paged")
  public Mono<PagedResponse<Payment>> getPaymentsPaged(
      @Parameter(description = "Número de página (por defecto 0)", example = "0") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Tamaño de la página (por defecto 10)", example = "10") @RequestParam(defaultValue = "10") int size) {
    return paymentService.getPaymentsPaged(page, size);
  }

  @Operation(summary = "Obtener pagos por estado de deuda", description = "Devuelve una lista de pagos que tienen o no tienen deuda.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida exitosamente.")
  })
  @GetMapping("/con-deuda")
  public Mono<PagedResponse<PaymentWithStudentDTO>> getPaymentsWithDebt(
      @Parameter(description = "Indica si se buscan pagos con deuda (true) o sin deuda (false)", required = true) @RequestParam Boolean hasDebt,
      @Parameter(description = "Número de página (por defecto 0)", example = "0") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Tamaño de la página (por defecto 10)", example = "10") @RequestParam(defaultValue = "10") int size) {
    return paymentService.getPaymentsHasDebt(hasDebt, page, size);
  }

  @Operation(summary = "Obtener pagos con deuda en un mes específico", description = "Devuelve una lista de pagos que tienen deuda en un mes y año específicos.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Lista de pagos con deuda obtenida exitosamente."),
      @ApiResponse(responseCode = "204", description = "No hay pagos con deuda en el período especificado.")
  })
  @GetMapping("/con-deuda/mes")
  public Mono<PagedResponse<PaymentWithStudentDTO>> getPaymentsWithDebtByMonth(
      @Parameter(description = "Año para filtrar los pagos", required = true, example = "2024") @RequestParam Integer year,
      @Parameter(description = "Mes para filtrar los pagos (1-12)", required = true, example = "1") @RequestParam Integer month,
      @Parameter(description = "Número de página (por defecto 0)", example = "0") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Tamaño de la página (por defecto 10)", example = "10") @RequestParam(defaultValue = "10") int size) {
    return paymentService.getPaymentsHasDebtByMonth(year, month, page, size);
  }

  @Operation(summary = "Obtener pago por ID", description = "Devuelve el detalle de un pago específico por su ID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pago encontrado exitosamente."),
      @ApiResponse(responseCode = "400", description = "Error en la solicitud."),
      @ApiResponse(responseCode = "404", description = "No se encontró el pago.")
  })
  @GetMapping("/{paymentId}")
  public Mono<ResponseEntity<PaymentWithStudentDTO>> getPaymentById(
      @Parameter(description = "ID del pago a buscar", required = true, example = "12345") @PathVariable String paymentId) {
    return paymentService.getPaymentWithDetailsById(paymentId)
        .map(ResponseEntity::ok)
        .switchIfEmpty(Mono.error(
            new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el pago con el ID: " + paymentId)));
  }

  @Operation(summary = "Obtener pagos por ID de estudiante", description = "Devuelve una lista de pagos realizados por un estudiante específico.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Lista de pagos del estudiante obtenida exitosamente."),
      @ApiResponse(responseCode = "404", description = "No se encontró el estudiante.")
  })
  @GetMapping("/estudiante/{studentId}")
  public Mono<PagedResponse<PaymentWithStudentDTO>> getPaymentsByStudentId(
      @Parameter(description = "ID del estudiante para buscar sus pagos realizados", required = true, example = "12345") @PathVariable String studentId,
      @Parameter(description = "Número de página (por defecto 0)", example = "0") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Tamaño de la página (por defecto 10)", example = "10") @RequestParam(defaultValue = "10") int size) {
    return paymentService.getAllPaymentsByStudentId(studentId, page, size);
  }

  @Operation(summary = "Registrar un nuevo pago", description = "Registra un nuevo pago para un curso y estudiante.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Pago registrado exitosamente."),
      @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados.")
  })
  @PostMapping
  public Mono<ResponseEntity<Payment>> registerPayment(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del pago a registrar", required = true) @RequestBody @Valid Payment payment) {
    return paymentService.registerPayment(payment, DEFAULT_USER)
        .map(savedPayment -> ResponseEntity.status(HttpStatus.CREATED).body(savedPayment));
  }

  @Operation(summary = "Realizar un pago parcial", description = "Permite realizar un abono parcial a un pago existente.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pago actualizado exitosamente."),
      @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados.")
  })
  @PutMapping("/realizar/{paymentId}")
  public Mono<ResponseEntity<Payment>> doPayment(
      @Parameter(description = "ID del pago al que se desea realizar el pago parcial", required = true, example = "12345") @PathVariable String paymentId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del pago parcial", required = true) @RequestBody @Valid Payment payment) {
    return paymentService.doPayment(paymentId, payment, DEFAULT_USER)
        .map(ResponseEntity::ok);
  }

  @Operation(summary = "Actualizar información de un pago", description = "Actualiza los datos principales de un pago existente.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pago actualizado exitosamente."),
      @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados.")
  })
  @PutMapping("/editar/{paymentId}")
  public Mono<ResponseEntity<Payment>> updatePaymentInfo(
      @Parameter(description = "ID del pago que se desea editar", required = true, example = "12345") @PathVariable String paymentId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados del pago", required = true) @RequestBody @Valid Payment payment) {
    return paymentService.updatePaymentInfo(paymentId, payment, DEFAULT_USER)
        .map(ResponseEntity::ok);
  }

  @Operation(summary = "Contar cuotas adeudadas", description = "Devuelve el número total de cuotas adeudadas en el sistema.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Número de cuotas adeudadas obtenido exitosamente."),
      @ApiResponse(responseCode = "404", description = "No se encontraron cuotas adeudadas.")
  })
  @GetMapping("/cuotas-adeudadas")
  public Mono<ResponseEntity<Long>> countDuePayments() {
    return paymentService.countPaymentHasDebt()
        .map(ResponseEntity::ok)
        .switchIfEmpty(
            Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron cuotas adeudadas")));
  }

  @Operation(summary = "Generar pagos mensuales", description = "Registra automáticamente los pagos mensuales de los estudiantes activos para los cursos en los que están inscritos.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pagos generados exitosamente."),
      @ApiResponse(responseCode = "500", description = "Error al generar los pagos mensuales.")
  })
  @PostMapping("/generar-cuotas")
  public Mono<ResponseEntity<Flux<Payment>>> generateMonthlyPayments() {
    return paymentService.registerMonthlyPayments(DEFAULT_USER)
        .collectList()
        .map(payments -> ResponseEntity.ok().body(Flux.fromIterable(payments)));
  }

  @DeleteMapping("/borrarTodo")
  public Mono<Void> eliminarTodo() {
    return paymentService.deleteAllPayment();
  }
}