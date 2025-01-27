package com.sistema.gestion.Controllers.Admin.Finance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.DTO.PaymentWithStudentDTO;
import com.sistema.gestion.Models.Admin.Finance.Payment;
import com.sistema.gestion.Services.Admin.Finance.PaymentService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pagos")
@Tag(name = "Pagos", description = "Operaciones relacionadas con los pagos de estudiantes")
public class PaymentController {

        @Autowired
        private final PaymentService paymentService;

        public PaymentController(PaymentService paymentService) {
                this.paymentService = paymentService;
        }

        @Operation(summary = "Obtener todos los pagos", description = "Devuelve un listado de todos los pagos registrados.", responses = {
                        @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida exitosamente."),
                        @ApiResponse(responseCode = "204", description = "No hay pagos registrados.")
        })
        @GetMapping("/todos")
        public Mono<ResponseEntity<Flux<Payment>>> getAllPayments() {
                return paymentService.getAllPayments()
                                .collectList()
                                .map(payments -> ResponseEntity.ok().body(Flux.fromIterable(payments)))
                                .defaultIfEmpty(ResponseEntity.noContent().build());
        }

        @Operation(summary = "Obtener todos los pagos con detalles", description = "Devuelve un listado de todos los pagos registrados junto con detalles de estudiantes y cursos.", responses = {
                        @ApiResponse(responseCode = "200", description = "Lista de pagos con detalles obtenida exitosamente."),
                        @ApiResponse(responseCode = "204", description = "No hay pagos registrados.")
        })
        @GetMapping("/todos-con-detalle")
        public Mono<ResponseEntity<Flux<PaymentWithStudentDTO>>> getAllPaymentsWithDetails() {
                return paymentService.getAllPaymentsDetails()
                                .collectList()
                                .map(payment -> ResponseEntity.ok().body(Flux.fromIterable(payment)))
                                .defaultIfEmpty(ResponseEntity.noContent().build());
        }

        @Operation(summary = "Obtener pago por ID", description = "Devuelve el detalle de un pago específico por su ID.", responses = {
                        @ApiResponse(responseCode = "200", description = "Pago encontrado exitosamente."),
                        @ApiResponse(responseCode = "400", description = "Error en la solicitud."),
                        @ApiResponse(responseCode = "404", description = "No se encontró el pago.")
        })
        @GetMapping("/{paymentId}")
        public Mono<ResponseEntity<PaymentWithStudentDTO>> getPaymentById(
                        @PathVariable @Parameter(description = "Es el ID del parametro que se desea buscar", required = true) String paymentId) {
                return paymentService.getPaymentWithDetailsById(paymentId)
                                .map(ResponseEntity::ok)
                                .onErrorMap(e -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                "Error al tratar de obtener el pago con el ID: " + paymentId))
                                .defaultIfEmpty(ResponseEntity.notFound().build());
        }

        @Operation(summary = "Obtener pagos por estado de deuda", description = "Devuelve una lista de pagos según si tienen deuda o no.", responses = {
                        @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida exitosamente."),
                        @ApiResponse(responseCode = "204", description = "No hay pagos que coincidan con el criterio.")
        })
        @GetMapping("/con-deuda/{conDeuda}")
        public Mono<ResponseEntity<Flux<PaymentWithStudentDTO>>> getPaymentWithDue(@PathVariable Boolean conDeuda) {
                return paymentService.getPaymentsHasDebt(conDeuda)
                                .collectList()
                                .map(payment -> ResponseEntity.ok().body(Flux.fromIterable(payment)))
                                .defaultIfEmpty(ResponseEntity.noContent().build());
        }

        @Operation(summary = "Obtener pagos con deuda en un mes específico", description = "Devuelve una lista de pagos que tienen deuda en un mes y año específicos.", responses = {
                        @ApiResponse(responseCode = "200", description = "Lista de pagos con deuda obtenida exitosamente."),
                        @ApiResponse(responseCode = "204", description = "No hay pagos con deuda en el período especificado.")
        })
        @GetMapping("/con-deuda/{anio}/{mes}")
        public Mono<ResponseEntity<Flux<PaymentWithStudentDTO>>> getPaymentWithDueByMonth(
                        @PathVariable @Parameter(description = "es el numero del año que se desea buscar", required = true) Integer anio,
                        @PathVariable @Parameter(description = "es el numero de mes que se desea buscar", required = true) Integer mes) {
                return paymentService.getPaymentsHasDebtByMonth(anio, mes)
                                .collectList()
                                .map(payment -> ResponseEntity.ok().body(Flux.fromIterable(payment)))
                                .defaultIfEmpty(ResponseEntity.noContent().build());
        }

        @Operation(summary = "Obtener pagos por ID de estudiante", description = "Devuelve una lista de pagos realizados por un estudiante específico.", responses = {
                        @ApiResponse(responseCode = "200", description = "Lista de pagos del estudiante obtenida exitosamente."),
                        @ApiResponse(responseCode = "404", description = "No se encontró el estudiante.")
        })
        @GetMapping("/estudiante/{studentId}")
        public Mono<ResponseEntity<Flux<PaymentWithStudentDTO>>> getPaymentsByStudentId(
                        @PathVariable @Parameter(description = "Es el ID del estudiante para buscar sus pagos realizados", required = true) String studentId) {
                return paymentService.getAllPaymentsByStudentId(studentId)
                                .collectList()
                                .map(payment -> ResponseEntity.ok().body(Flux.fromIterable(payment)))
                                .onErrorMap(e -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Error al obtener los datos del pago para el Id del estudiante: "
                                                                + studentId))
                                .defaultIfEmpty(ResponseEntity.noContent().build());
        }

        @Operation(summary = "Registrar un nuevo pago", description = "Registra un nuevo pago para un curso y estudiante.", responses = {
                        @ApiResponse(responseCode = "201", description = "Pago registrado exitosamente."),
                        @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados.")
        })
        @PostMapping
        public Mono<ResponseEntity<Payment>> registerPayment(@RequestBody @Valid Payment payment) {
                String user = "ADMIN"; // Se cambia cuando se implemente la seguridad
                return paymentService.registerPayment(payment, user)
                                .map(savedPayment -> ResponseEntity.status(HttpStatus.CREATED).body(savedPayment))
                                .onErrorMap(e -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                "Error al registrar el pago para el curso ID: "
                                                                + payment.getCourseId()));
        }

        @Operation(summary = "Realizar un pago parcial", description = "Permite realizar un abono parcial a un pago existente.", responses = {
                        @ApiResponse(responseCode = "200", description = "Pago actualizado exitosamente."),
                        @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados.")
        })
        @PutMapping("/realizar/{paymentId}")
        public Mono<ResponseEntity<Payment>> doPayment(
                        @PathVariable @Parameter(description = "Es el Id del pago al que se desea realizar el pago parcial", required = true) String paymentId,
                        @RequestBody @Valid Payment payment) {
                String user = "ADMIN"; // Se cambia cuando se implemente la seguridad
                return paymentService.doPayment(paymentId, payment, user)
                                .map(updatedPayment -> ResponseEntity.ok(updatedPayment))
                                .onErrorMap(e -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                "Error al realizar el pago con el ID: " + paymentId));
        }

        @Operation(summary = "Actualizar información de un pago", description = "Actualiza los datos principales de un pago existente.", responses = {
                        @ApiResponse(responseCode = "200", description = "Pago actualizado exitosamente."),
                        @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados.")
        })
        @PutMapping("/editar/{paymentId}")
        public Mono<ResponseEntity<Payment>> updatePaymentInfo(
                        @PathVariable @Parameter(description = "Es el ID del pago que se desea editar", required = true) String paymentId,
                        @RequestBody @Valid Payment payment) {
                String usuario = "ADMIN"; // Se cambia cuando se implemente la seguridad
                return paymentService.updatePaymentInfo(paymentId, payment, usuario)
                                .map(pagoActualizado -> ResponseEntity.ok(pagoActualizado))
                                .onErrorMap(e -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                "Error al editar la información del pago con ID: " + paymentId));
        }

        @Operation(summary = "Contar cuotas adeudadas", description = "Devuelve el número total de cuotas adeudadas en el sistema.", responses = {
                        @ApiResponse(responseCode = "200", description = "Número de cuotas adeudadas obtenido exitosamente."),
                        @ApiResponse(responseCode = "404", description = "No se encontraron cuotas adeudadas.")
        })
        @GetMapping("/cuotas-adeudadas")
        public Mono<ResponseEntity<Long>> countDuePayments() {
                return paymentService.countPaymentHasDebt()
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
        }

        @Operation(summary = "Generar pagos mensuales", description = "Registra automáticamente los pagos mensuales de los estudiantes activos para los cursos en los que están inscritos.", responses = {
                        @ApiResponse(responseCode = "200", description = "Pagos generados exitosamente"),
                        @ApiResponse(responseCode = "500", description = "Error al generar los pagos mensuales")
        })
        @PostMapping("/generar-cuotas")
        public Mono<ResponseEntity<Flux<Payment>>> generateMonthlyPayments() {
                String usuario = "ADMIN"; // Se cambia cuando se implemente la seguridad

                return paymentService.registerMonthlyPayments(usuario)
                                .collectList()
                                .map(payments -> ResponseEntity.ok().body(Flux.fromIterable(payments)))
                                .defaultIfEmpty(ResponseEntity.noContent().build());
        }

        /*
         * @DeleteMapping("/borrarTodo")
         * public Mono<Void> eliminarTodo() {
         * return paymentService.deleteAllPayment();
         * }
         */
}
