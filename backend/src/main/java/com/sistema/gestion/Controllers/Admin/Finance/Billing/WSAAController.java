package com.sistema.gestion.Controllers.Admin.Finance.Billing;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistema.gestion.Services.Admin.Finance.Billing.WSAAClientService;

import reactor.core.publisher.Mono;

@RestController
public class WSAAController {

    private final WSAAClientService wsaaClient;

    public WSAAController(WSAAClientService wsaaClient) {
        this.wsaaClient = wsaaClient;
    }

    @GetMapping("/authenticate")
    public Mono<ResponseEntity<String>> authenticate() {
        return wsaaClient.authenticate()
                .map(ResponseEntity::ok);
    }
}

