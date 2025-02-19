package com.sistema.gestion.Controllers.Admin.Finance.Billing;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sistema.gestion.Services.Admin.Finance.Billing.CertificateService;

import reactor.core.publisher.Mono;

@RestController
public class CertificateController {
	private final CertificateService certificateService;

	public CertificateController(CertificateService certificateService) {
		this.certificateService = certificateService;
	}

	@GetMapping("/generate-csr")
	public Mono<ResponseEntity<String>> generateCsr(@RequestParam String cuit) {
		return Mono.fromCallable(() -> {
			certificateService.generateCSR(cuit);
			return ResponseEntity.ok("CSR y clave privada generados.");
		});
	}
}

