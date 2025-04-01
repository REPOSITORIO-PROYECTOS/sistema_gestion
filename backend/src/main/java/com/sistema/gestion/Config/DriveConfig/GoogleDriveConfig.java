package com.sistema.gestion.Config.DriveConfig;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.Drive;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class GoogleDriveConfig {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Bean
    public Drive googleDriveService() throws IOException, GeneralSecurityException {
        // Cargar credenciales desde el archivo JSON
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(new ClassPathResource("credentials.json").getFile()))
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

