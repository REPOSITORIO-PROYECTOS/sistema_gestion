package com.sistema.gestion.Services.Admin.Finance.Billing;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import javax.security.auth.x500.X500Principal;

import lombok.extern.slf4j.Slf4j;

import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CertificateService {

    public void generateCSR(String cuit) throws Exception {
        // Generar par de claves (RSA 2048)
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();

        // Guardar clave privada
        savePrivateKey(privateKey, "clavePrivada.key");

        // Datos del CSR (CUIT en Common Name)
        X500Principal subject = new X500Principal("CN=" + cuit + ", O=Mi Empresa, C=AR");

        // Construir CSR
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(privateKey);
        JcaPKCS10CertificationRequestBuilder p10Builder =
                new JcaPKCS10CertificationRequestBuilder(subject, keyPair.getPublic());
        PKCS10CertificationRequest csr = p10Builder.build(signer);

        // Guardar el CSR
        saveCSR(csr, "solicitud.csr");

        log.info("CSR y clave privada generados correctamente.");
    }

    private void savePrivateKey(PrivateKey privateKey, String fileName) throws Exception {
        File file = new File(fileName);
        Files.write(file.toPath(), privateKey.getEncoded());
        log.info("Clave privada guardada en: " + fileName);
    }

    private void saveCSR(PKCS10CertificationRequest csr, String fileName) throws Exception {
        try (JcaPEMWriter writer = new JcaPEMWriter(new FileWriter(fileName))) {
            writer.writeObject(csr);
            log.info("CSR guardado en: " + fileName);
        }
        
    }
}

