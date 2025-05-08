package com.sistema.gestion.Services.Admin.Management;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.sistema.gestion.Models.Admin.Management.GoogleDriveCredentials;
import com.sistema.gestion.Repositories.Admin.Management.GoogleDriveCredentialsRepository;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GoogleDriveCredentialsService {

    private final GoogleDriveCredentialsRepository googleDriveCredentialsRepository;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * Recupera las credenciales de Google Drive desde MongoDB.
     *
     * @return Mono de GoogleDriveCredentials
     */
    public Mono<GoogleDriveCredentials> getGoogleDriveCredentials() {
        return googleDriveCredentialsRepository.findAll()
                .next()
                .switchIfEmpty(Mono.error(new RuntimeException("Google Drive credentials not found")));
    }

    /**
     * Recupera solo el `folderId` de Google Drive desde MongoDB.
     *
     * @return Mono con el folderId
     */
    public Mono<String> getFolderId() {
        return googleDriveCredentialsRepository.findAll()
                .next()
                .map(GoogleDriveCredentials::getFolderId)
                .switchIfEmpty(Mono.error(new RuntimeException("No folderId found in Google Drive credentials")));
    }

    /**
     * Guarda las credenciales de Google Drive en MongoDB.
     *
     * @param credentials las credenciales que se van a guardar
     * @return Mono del documento guardado
     */
    public Mono<GoogleDriveCredentials> saveGoogleDriveCredentials(GoogleDriveCredentials credentials) {
        return googleDriveCredentialsRepository.save(credentials);
    }

    /**
     * Actualiza las credenciales de Google Drive en MongoDB.
     *
     * @param credentials las credenciales que se van a actualizar
     * @return Mono del documento actualizado
     */
    public Mono<GoogleDriveCredentials> updateGoogleDriveCredentials(GoogleDriveCredentials credentials) {
        return googleDriveCredentialsRepository.findAll()
                .next()
                .flatMap(existingCredentials -> {
                    existingCredentials.setCredentialsJson(credentials.getCredentialsJson());
                    existingCredentials.setFolderId(credentials.getFolderId());
                    return googleDriveCredentialsRepository.save(existingCredentials);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Google Drive credentials not found for update")));
    }

    public Mono<Drive> googleDriveService() {
        return googleDriveCredentialsRepository.findById("6812270ac9eca99f800178b2")
            .switchIfEmpty(Mono.error(new IOException("No Google Drive credentials found in the client's database.")))
            //.next() // Toma el único documento esperado
            .flatMap(credential -> {
                try {
                    GoogleCredentials googleCredentials = GoogleCredentials
                            .fromStream(new ByteArrayInputStream(credential.getCredentialsJson().getBytes()))
                            .createScoped(Collections.singletonList(DriveScopes.DRIVE));
    
                    HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(googleCredentials);
    
                    Drive drive = new Drive.Builder(
                            GoogleNetHttpTransport.newTrustedTransport(),
                            JSON_FACTORY,
                            requestInitializer
                    ).setApplicationName("APPLICATION_NAME").build();
    
                    return Mono.just(drive);
    
                } catch (IOException | GeneralSecurityException e) {
                    return Mono.error(new RuntimeException("Error creating Google Drive service", e));
                }
            });
    }
    

}



//@Configuration
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