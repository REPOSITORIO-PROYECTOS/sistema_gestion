package com.sistema.gestion.Services.Admin.Finance;

import java.time.LocalDateTime;

import com.sistema.gestion.Services.Profiles.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sistema.gestion.DTO.InvoiceWithProviderDTO;
import com.sistema.gestion.DTO.PagedResponse;
import com.sistema.gestion.Models.Admin.Finance.Invoice;
import com.sistema.gestion.Models.Admin.Finance.Provider;
import com.sistema.gestion.Repositories.Admin.Finance.CashRegisterRepository;
import com.sistema.gestion.Repositories.Admin.Finance.InvoiceRepository;
import com.sistema.gestion.Repositories.Admin.Finance.ProviderRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class InvoiceService {

	private final InvoiceRepository invoiceRepo;
	private final ProviderRepository providerRepo;
	private final CashRegisterRepository cashRegisterRepo;
	private final UserService userService;

	public Mono<PagedResponse<Invoice>> getInvoicesPaged(int page, int size) {
		PageRequest pageRequest = PageRequest.of(page, size);
		Mono<Long> totalElementsMono = invoiceRepo.count();
		Flux<Invoice> invoicesFlux = invoiceRepo.findInvoicesPaged(pageRequest);

		return Mono.zip(totalElementsMono, invoicesFlux.collectList())
				.map(tuple -> new PagedResponse<>(
						tuple.getT2(), // Lista de facturas
						tuple.getT1(), // Total de registros
						page,
						size));
	}

	public Mono<InvoiceWithProviderDTO> getInvoiceWithDetails(String invoiceId) {
		return invoiceRepo.findById(invoiceId)
				.switchIfEmpty(Mono
						.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la factura con ID" + invoiceId)))
				.flatMap(invoice -> providerRepo.findById(invoice.getProviderId())
						.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
								"No se encontró proveedor con el ID: " + invoice.getProviderId())))
						.flatMap(provider -> mappingFromInvoiceToInvoiceWithProviderDTO(invoice, provider)));
	}

	public Mono<Invoice> saveInvoice(Invoice invoice, String username) {
		if (invoice.getId() != null && !invoice.getId().isEmpty()) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "La factura ya tiene un ID registrado" +
					" No se puede almacenar un proveedor con Id ya registrado."));
		}


		return userService.getFullName(username)
				.flatMap(name -> {

					invoice.setPaidAmount(0.0);
					invoice.setCreatedAt(LocalDateTime.now());
					invoice.setCreatedBy(name);

					return invoiceRepo.save(invoice);
				});
	}

	public Mono<Invoice> updateInvoice(Invoice invoice, String invoiceId, String username) {
		if (!invoice.getId().equals(invoiceId)) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los IDs del proveedor a actualizar " +
					"en la base de datos con el del cuerpo de la solicitud no coinciden."));
		}

		return userService.getFullName(username)
				.flatMap(name -> {
					return invoiceRepo.findById(invoiceId)
							.flatMap(existingInvoice -> {
								return invoiceRepo.save(mappingInvoiceToUpdate(existingInvoice, invoice, name));
							});
				});

	}

	public Mono<Invoice> doInvoicePayment(String invoiceId, Invoice invoice, String username) {
		if (!invoice.getId().equals(invoiceId)) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Los IDs del Pago a efectuar " +
							"en la base de datos con el del cuerpo de la solicitud no coinciden."
							+
							"ID solicitud: " + invoice.getId() + "\nID base de datos: " + invoiceId));
		}

		return userService.getFullName(username)
				.flatMap(name -> {
					return cashRegisterRepo.findFirstByIsClosedFalse()
							.hasElement() // Verifica si hay elementos
							.flatMap(hasOpenRegister -> {
								if (hasOpenRegister) {
									return invoiceRepo.findById(invoiceId)
											.flatMap(existingInvoice -> {
												if (existingInvoice.getDueAmount() < (existingInvoice.getPaidAmount() + invoice.getPaidAmount())) {
													return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
															"El pago a realizar exederá la deuda total."));
												}
												existingInvoice.setPaidAmount(existingInvoice.getPaidAmount() + invoice.getPaidAmount());
												existingInvoice.setLastPaymentDate(LocalDateTime.now());
												existingInvoice.setUpdatedAt(LocalDateTime.now());
												existingInvoice.setModifiedBy(name);

												existingInvoice.setHasDebt(existingInvoice.getPaidAmount() < existingInvoice.getDueAmount());
												existingInvoice.setIsPaid(existingInvoice.getPaidAmount() >= existingInvoice.getDueAmount());
												return invoiceRepo.save(existingInvoice);
											});
								}
								return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
										"No existe una caja abierta, para guarar un pago necesita abrir la caja primero."));
							});
				});

	}

	/** Métodos locales */
	private Mono<InvoiceWithProviderDTO> mappingFromInvoiceToInvoiceWithProviderDTO(Invoice invoice, Provider provider) {
		InvoiceWithProviderDTO dto = new InvoiceWithProviderDTO();

		// Mapear datos de la Factura
		dto.setInvoiceId(invoice.getId());
		dto.setInvoiceDescription(invoice.getDescription());
		dto.setInvoiceDueAmount(invoice.getDueAmount());
		dto.setInvoicePaidAmount(invoice.getPaidAmount());
		dto.setInvoicePaymentDueDate(invoice.getPaymentDueDate());
		dto.setInvoiceLastPaymentDate(invoice.getLastPaymentDate());

		// Mapear datos del Proveedor
		dto.setProviderId(provider.getId());
		dto.setProviderName(provider.getName());
		dto.setProviderCuitCuil(provider.getCuilCuit());
		dto.setProviderAddress(provider.getAddress());
		dto.setProviderPhone(provider.getPhone());

		return Mono.just(dto);
	}

	private Invoice mappingInvoiceToUpdate(Invoice existingInvoice, Invoice invoice, String user) {
		if (invoice.getDescription() != null && !invoice.getDescription().isEmpty()) {
			existingInvoice.setDescription(invoice.getDescription());
		}
		if (invoice.getDueAmount() != null && invoice.getDueAmount() >= 0) {
			existingInvoice.setDueAmount(invoice.getDueAmount());
		}
		if (invoice.getPaidAmount() != null && invoice.getPaidAmount() >= 0) {
			existingInvoice.setPaidAmount(invoice.getPaidAmount());
		}
		if (invoice.getPaymentDueDate() != null) {
			existingInvoice.setPaymentDueDate(invoice.getPaymentDueDate());
		}
		if (invoice.getLastPaymentDate() != null) {
			existingInvoice.setLastPaymentDate(invoice.getLastPaymentDate());
		}
		if (invoice.getProviderId() != null && !invoice.getProviderId().isEmpty()) {
			existingInvoice.setProviderId(invoice.getProviderId());
		}

		existingInvoice.setUpdatedAt(LocalDateTime.now());
		existingInvoice.setModifiedBy(user);

		return existingInvoice;
	}

}