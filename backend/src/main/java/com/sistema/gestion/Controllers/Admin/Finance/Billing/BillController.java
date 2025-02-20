package com.sistema.gestion.Controllers.Admin.Finance.Billing;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sistema.gestion.Services.Admin.Finance.Billing.WSAAClientService;
import com.sistema.gestion.Services.Admin.Finance.Billing.WSFEClientService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/afip/factura")
public class BillController {

    private final WSFEClientService wsfeClient;
    private final WSAAClientService wsaaClient;

    public BillController(WSFEClientService wsfeClient, WSAAClientService wsaaClient) {
        this.wsfeClient = wsfeClient;
        this.wsaaClient = wsaaClient;
    }

    @GetMapping("/facturar")
    public Mono<ResponseEntity<String>> facturar(@RequestParam String cuit) {
        return wsaaClient.authenticate()
                .flatMap(response -> {
                    String token = extractValue(response, "Token");
                    String sign = extractValue(response, "Sign");
                    return wsfeClient.createInvoice(token, sign, cuit);
                })
                .map(ResponseEntity::ok);
    }

    @GetMapping("/consultar")
    public Mono<ResponseEntity<String>> consultarFactura(
            @RequestParam String cuit,
            @RequestParam int ptoVta,
            @RequestParam int tipoComprobante,
            @RequestParam int numeroComprobante) {

        return wsaaClient.authenticate()
                .flatMap(response -> {
                    String token = extractValue(response, "Token");
                    String sign = extractValue(response, "Sign");

                    return wsfeClient.consultarFactura(token, sign, cuit, ptoVta, tipoComprobante, numeroComprobante);
                })
                .map(ResponseEntity::ok);
    }

    private String extractValue(String response, String tagName) {
        int start = response.indexOf("<" + tagName + ">") + tagName.length() + 2;
        int end = response.indexOf("</" + tagName + ">");
        return response.substring(start, end);
    }
}

