package com.sistema.gestion.Config.DriveConfig;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.sistema.gestion.Models.Admin.Management.GoogleDriveCredentials;
import com.sistema.gestion.Repositories.Admin.Management.GoogleDriveCredentialsRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.Drive;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

// @Configuration
// @RequiredArgsConstructor
// public class GoogleDriveConfig {

//     private static final String GOOGLE_DRIVE_CREDENTIALS_ID = "6812270ac9eca99f800178b2";  // ID único en Mongo
//     private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

//     private final GoogleDriveCredentialsRepository googleDriveCredentialsRepository;

//     @Bean
//     public Mono<Drive> googleDriveService() {
//         // Cargar las credenciales desde MongoDB de manera reactiva
//         return googleDriveCredentialsRepository.findAll()
//                 .switchIfEmpty(Mono.error(new IOException("No Google Drive credentials found in the database."))) // Manejo de error reactivo si no hay credenciales
//                 .next()  // Tomamos solo el primer elemento del flujo (sin bloquear)
//                 .flatMap(firstCredential -> {
//                     try {
//                         // Convertir las credenciales JSON a GoogleCredentials
//                         GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ByteArrayInputStream(firstCredential.getCredentialsJson().getBytes()))
//                                 .createScoped(Collections.singletonList(DriveScopes.DRIVE));

//                         // Convertir GoogleCredentials a HttpRequestInitializer
//                         HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(googleCredentials);

//                         // Construir la instancia de Google Drive
//                         return Mono.just(new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, requestInitializer)
//                                 .setApplicationName("APPLICATION_NAME")
//                                 .build());
//                     } catch (IOException | GeneralSecurityException e) {
//                         return Mono.error(new RuntimeException("Error creating Google Drive service", e)); // Manejo de excepciones
//                     }
//                 });
//     }
// }



@Configuration
public class GoogleDriveConfig {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Bean
    public Drive googleDriveService() throws IOException, GeneralSecurityException {
        // Cargar credenciales desde el archivo JSON
        GoogleCredentials credentials = GoogleCredentials.fromStream(new ClassPathResource("credentials.json").getInputStream())
                .createScoped(Collections.singletonList(DriveScopes.DRIVE));

        // Convertir GoogleCredentials a HttpRequestInitializer
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
        
        // Construir la instancia de Google Drive
        Drive driveInstance = new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, requestInitializer)
        .setApplicationName("APPLICATION")
        .build(); 
        
        return driveInstance;
    }
}

