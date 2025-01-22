package com.sistema.gestion.Controllers.Admin.Finance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistema.gestion.DTO.InvoiceWithProviderDTO;
import com.sistema.gestion.Models.Admin.Finance.Invoice;
import com.sistema.gestion.Services.Admin.Finance.InvoiceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/facturas")
@Tag(name = "Invoice Controller", description = "Controlador para la gestión de facturas")
public class InvoiceController {

  @Autowired
  private final InvoiceService invoiceService;

  public InvoiceController(InvoiceService invoiceService) {
    this.invoiceService = invoiceService;
  }

  @Operation(summary = "Obtener todas las facturas", description = "Retorna una lista de todas las facturas registradas")
  @ApiResponse(responseCode = "200", description = "Lista de facturas obtenida exitosamente")
  @GetMapping
  public Flux<Invoice> getAllInvoices() {
    return invoiceService.getAllInvoices();
  }

  @Operation(summary = "Obtener todas las facturas con detalles", description = "Retorna una lista de facturas con detalles de proveedor")
  @ApiResponse(responseCode = "200", description = "Lista de facturas con detalles obtenida exitosamente")
  @GetMapping("/detalles")
  public Flux<InvoiceWithProviderDTO> getAllInvoicesWithDetails() {
    return invoiceService.getAllInvoicesWithDetails();
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
      @RequestBody Invoice invoice) {
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
