package com.sistema.gestion.Controllers.Admin.Finance;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sistema.gestion.DTO.InvoiceWithProviderDTO;
import com.sistema.gestion.DTO.PagedResponse;
import com.sistema.gestion.Models.Admin.Finance.Invoice;
import com.sistema.gestion.Services.Admin.Finance.InvoiceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/facturas")
@Tag(name = "Invoice Controller", description = "Controlador para la gestión de facturas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InvoiceController {

  private final InvoiceService invoiceService;

  @GetMapping("/paged")
  public Mono<PagedResponse<Invoice>> getInvoicesPaged(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return invoiceService.getInvoicesPaged(page, size);
  }

  @Operation(summary = "Obtener una factura con detalles", description = "Retorna los detalles de una factura específica por su ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Factura con detalles obtenida exitosamente"),
      @ApiResponse(responseCode = "404", description = "Factura o proveedor no encontrado")
  })
  @GetMapping("/detalles/{invoiceId}")
  public Mono<ResponseEntity<InvoiceWithProviderDTO>> getInvoiceWithDetails(
      @Parameter(description = "ID de la factura a buscar", required = true) @PathVariable String invoiceId) {
    return invoiceService.getInvoiceWithDetails(invoiceId)
        .map(ResponseEntity::ok);
  }

  @Operation(summary = "Registrar una nueva factura", description = "Crea una nueva factura en el sistema")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Factura creada exitosamente"),
      @ApiResponse(responseCode = "400", description = "Solicitud inválida")
  })
  @PostMapping
  public Mono<ResponseEntity<Invoice>> saveInvoice(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la factura") @RequestBody Invoice invoice) {
    String user = "ADMIN"; // Se cambia cuando se implemente la seguridad
    return invoiceService.saveInvoice(invoice, user)
        .map(savedInvoice -> ResponseEntity.status(HttpStatus.CREATED).body(savedInvoice));
  }

  @Operation(summary = "Actualizar una factura", description = "Actualiza los datos de una factura existente")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Factura actualizada exitosamente"),
      @ApiResponse(responseCode = "400", description = "IDs no coinciden o solicitud inválida"),
      @ApiResponse(responseCode = "404", description = "Factura no encontrada")
  })
  @PutMapping("/{invoiceId}")
  public Mono<ResponseEntity<Invoice>> updateInvoice(@RequestBody Invoice invoice, @PathVariable String invoiceId) {
    String user = "ADMIN"; // Se cambia cuando se implemente la seguridad
    return invoiceService.updateInvoice(invoice, invoiceId, user)
        .map(updatedInvoice -> ResponseEntity.ok(updatedInvoice));
  }

  @Operation(summary = "Registrar un pago de factura", description = "Efectúa un pago sobre una factura existente")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pago realizado exitosamente"),
      @ApiResponse(responseCode = "400", description = "Solicitud inválida o caja cerrada"),
      @ApiResponse(responseCode = "404", description = "Factura no encontrada")
  })
  @PutMapping("/pagar/{invoiceId}")
  public Mono<ResponseEntity<Invoice>> doInvoicePayment(@RequestBody Invoice invoice, @PathVariable String invoiceId) {
    String user = "ADMIN"; // Se cambia cuando se implemente la seguridad
    return invoiceService.doInvoicePayment(invoiceId, invoice, user)
        .map(paidInvoice -> ResponseEntity.ok(paidInvoice));
  }
}