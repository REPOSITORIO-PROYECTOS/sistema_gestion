package com.sistema.gestion.Services.Admin.Finance.Billing;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Service
public class AfipAuthService {

    private static final String CERT_PATH = "src/main/resources/afip/certificado.crt";
    private static final String KEY_PATH = "src/main/resources/afip/clavePrivada.key";
    private static final String WSAA_URL = "https://wsaahomo.afip.gov.ar/ws/services/LoginCms";
    private static final String SERVICE = "wsfe";

    public String generateTRA() throws Exception {
        long generationTime = System.currentTimeMillis() / 1000;
        long expirationTime = generationTime + (12 * 60 * 60); // 12 horas

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        // Crear estructura XML del TRA
        Element loginTicketRequest = doc.createElement("loginTicketRequest");
        loginTicketRequest.setAttribute("version", "1.0");
        doc.appendChild(loginTicketRequest);

        Element header = doc.createElement("header");
        loginTicketRequest.appendChild(header);

        Element uniqueId = doc.createElement("uniqueId");
        uniqueId.appendChild(doc.createTextNode(String.valueOf(generationTime)));
        header.appendChild(uniqueId);

        Element generationTimeElem = doc.createElement("generationTime");
        generationTimeElem.appendChild(doc.createTextNode(formatAfipDate(generationTime)));
        header.appendChild(generationTimeElem);

        Element expirationTimeElem = doc.createElement("expirationTime");
        expirationTimeElem.appendChild(doc.createTextNode(formatAfipDate(expirationTime)));
        header.appendChild(expirationTimeElem);

        Element serviceElem = doc.createElement("service");
        serviceElem.appendChild(doc.createTextNode(SERVICE));
        loginTicketRequest.appendChild(serviceElem);

        // Convertir el XML a String
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(doc), new StreamResult(outputStream));
        String xmlTRA = outputStream.toString();

        // Firmar el XML con la clave privada
        return signTRA(xmlTRA);
    }

    private String signTRA(String xml) throws Exception {
        byte[] privateKeyBytes = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(KEY_PATH));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(xml.getBytes());
        byte[] signedData = signature.sign();

        return Base64.getEncoder().encodeToString(signedData);
    }

    private String formatAfipDate(long timestamp) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new java.util.Date(timestamp * 1000));
    }
}

