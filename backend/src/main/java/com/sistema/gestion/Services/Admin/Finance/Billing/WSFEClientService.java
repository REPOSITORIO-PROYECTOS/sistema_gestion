package com.sistema.gestion.Services.Admin.Finance.Billing;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WSFEClientService {

    private final WebClient webClient;
    private static final String WSFE_URL = "https://wswhomo.afip.gov.ar/wsfev1/service.asmx";

    public WSFEClientService() {
        this.webClient = WebClient.create(WSFE_URL);
    }

    public Mono<String> createInvoice(String token, String sign, String cuit) {
        String soapRequest = buildSoapRequest(token, sign, cuit);
        return webClient.post()
                .contentType(MediaType.TEXT_XML)
                .bodyValue(soapRequest)
                .retrieve()
                .bodyToMono(String.class);
    }

    private String buildSoapRequest(String token, String sign, String cuit) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                + "xmlns:wsfe=\"http://ar.gov.afip.dif.FEV1/\">"
                + "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<wsfe:FECAESolicitar>"
                + "<wsfe:Auth>"
                + "<wsfe:Token>" + token + "</wsfe:Token>"
                + "<wsfe:Sign>" + sign + "</wsfe:Sign>"
                + "<wsfe:Cuit>" + cuit + "</wsfe:Cuit>"
                + "</wsfe:Auth>"
                + "<wsfe:FeCAEReq>"
                + "<wsfe:FeCabReq>"
                + "<wsfe:CantReg>1</wsfe:CantReg>"
                + "<wsfe:PtoVta>1</wsfe:PtoVta>"
                + "<wsfe:CbteTipo>6</wsfe:CbteTipo>" // Factura B
                + "</wsfe:FeCabReq>"
                + "<wsfe:FeDetReq>"
                + "<wsfe:FECAEDetRequest>"
                + "<wsfe:Concepto>1</wsfe:Concepto>" // Productos
                + "<wsfe:DocTipo>80</wsfe:DocTipo>" // CUIT
                + "<wsfe:DocNro>20123456789</wsfe:DocNro>"
                + "<wsfe:CbteDesde>1</wsfe:CbteDesde>"
                + "<wsfe:CbteHasta>1</wsfe:CbteHasta>"
                + "<wsfe:CbteFch>" + getCurrentDate() + "</wsfe:CbteFch>"
                + "<wsfe:ImpTotal>121.00</wsfe:ImpTotal>"
                + "<wsfe:ImpTotConc>0.00</wsfe:ImpTotConc>"
                + "<wsfe:ImpNeto>100.00</wsfe:ImpNeto>"
                + "<wsfe:ImpIVA>21.00</wsfe:ImpIVA>"
                + "<wsfe:ImpTrib>0.00</wsfe:ImpTrib>"
                + "<wsfe:ImpOpEx>0.00</wsfe:ImpOpEx>"
                + "<wsfe:FchServDesde></wsfe:FchServDesde>"
                + "<wsfe:FchServHasta></wsfe:FchServHasta>"
                + "<wsfe:FchVtoPago></wsfe:FchVtoPago>"
                + "<wsfe:MonId>PES</wsfe:MonId>"
                + "<wsfe:MonCotiz>1.00</wsfe:MonCotiz>"
                + "<wsfe:Iva>"
                + "<wsfe:AlicIva>"
                + "<wsfe:Id>5</wsfe:Id>" // 21% IVA
                + "<wsfe:BaseImp>100.00</wsfe:BaseImp>"
                + "<wsfe:Importe>21.00</wsfe:Importe>"
                + "</wsfe:AlicIva>"
                + "</wsfe:Iva>"
                + "</wsfe:FECAEDetRequest>"
                + "</wsfe:FeDetReq>"
                + "</wsfe:FeCAEReq>"
                + "</wsfe:FECAESolicitar>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";
    }

    public Mono<String> consultarFactura(String token, String sign, String cuit, int puntoVenta, int tipoComprobante, int numeroComprobante) {
        String soapRequest = buildSoapRequest(token, sign, cuit, puntoVenta, tipoComprobante, numeroComprobante);

        return webClient.post()
                .contentType(MediaType.TEXT_XML)
                .bodyValue(soapRequest)
                .retrieve()
                .bodyToMono(String.class);
    }

    private String buildSoapRequest(String token, String sign, String cuit, int ptoVta, int cbteTipo, int cbteNro) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                + "xmlns:wsfe=\"http://ar.gov.afip.dif.FEV1/\">"
                + "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<wsfe:FECompConsultar>"
                + "<wsfe:Auth>"
                + "<wsfe:Token>" + token + "</wsfe:Token>"
                + "<wsfe:Sign>" + sign + "</wsfe:Sign>"
                + "<wsfe:Cuit>" + cuit + "</wsfe:Cuit>"
                + "</wsfe:Auth>"
                + "<wsfe:FeCompConsReq>"
                + "<wsfe:PtoVta>" + ptoVta + "</wsfe:PtoVta>"
                + "<wsfe:CbteTipo>" + cbteTipo + "</wsfe:CbteTipo>"
                + "<wsfe:CbteNro>" + cbteNro + "</wsfe:CbteNro>"
                + "</wsfe:FeCompConsReq>"
                + "</wsfe:FECompConsultar>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";
    }

    private String getCurrentDate() {
        return new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
    }
}

