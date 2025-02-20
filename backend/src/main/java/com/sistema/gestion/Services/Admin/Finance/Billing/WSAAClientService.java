package com.sistema.gestion.Services.Admin.Finance.Billing;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import reactor.core.publisher.Mono;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

@Service
public class WSAAClientService {

    private final WebClient webClient;
    private final AfipAuthService authService;

    public WSAAClientService(AfipAuthService authService) {
        this.webClient = WebClient.create("https://wsaahomo.afip.gov.ar/ws/services/LoginCms");
        this.authService = authService;
    }

    public Mono<String> authenticate() {
        return Mono.fromCallable(() -> authService.generateTRA())
            .flatMap(this::sendRequestToAfip)
            .flatMap(response -> Mono.fromCallable(() -> extractTokenAndSign(response)))
            .onErrorResume(e -> {
                return Mono.error(new Error("Error en la autenticación con AFIP", e));
            });
    }
    

    private Mono<String> sendRequestToAfip(String signedTRA) {
        String soapRequest = buildSoapRequest(signedTRA);
        /*TODO Ejecucion normal hasta aca
         * Se obtuvo el TRA codificado en base64 
         * y la siguente linea lo envia a AFIP 
         * para obtener el Token de acceso
        */
        WebClient webClient = WebClient.create();

        return webClient.post()
                .contentType(MediaType.TEXT_XML)
                .bodyValue(soapRequest)
                .retrieve()
                .bodyToMono(String.class);
    }

    private String buildSoapRequest(String signedTRA) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:wsaa=\"http://wsaa.view.afip.gov.ar/\">"
                + "   <soapenv:Header/>"
                + "   <soapenv:Body>"
                + "      <wsaa:loginCms>"
                + "         <wsaa:in>" + signedTRA + "</wsaa:in>"
                + "      </wsaa:loginCms>"
                + "   </soapenv:Body>"
                + "</soapenv:Envelope>";
    }

    private String extractTokenAndSign(String response) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(response)));

        String token = document.getElementsByTagName("token").item(0).getTextContent();
        String sign = document.getElementsByTagName("sign").item(0).getTextContent();

        return "Token: " + token + "\nSign: " + sign;
    }
}

