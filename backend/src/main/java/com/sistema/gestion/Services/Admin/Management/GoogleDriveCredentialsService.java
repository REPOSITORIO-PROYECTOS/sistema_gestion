package com.sistema.gestion.Services.Admin.Management;
import com.sistema.gestion.Models.Admin.Management.GoogleDriveCredentials;
import com.sistema.gestion.Repositories.Admin.Management.GoogleDriveCredentialsRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GoogleDriveCredentialsService {

    private static final String CREDENTIALS_ID = "6812270ac9eca99f800178b2";  // ID único en MongoDB

    private final GoogleDriveCredentialsRepository googleDriveCredentialsRepository;

    /**
     * Recupera las credenciales de Google Drive desde MongoDB.
     *
     * @return Mono de GoogleDriveCredentials
     */
    public Mono<GoogleDriveCredentials> getGoogleDriveCredentials() {
        return googleDriveCredentialsRepository.findById(CREDENTIALS_ID)
                .switchIfEmpty(Mono.error(new RuntimeException("Google Drive credentials not found")));
    }

    /**
     * Recupera solo el `folderId` de Google Drive desde MongoDB.
     *
     * @return Mono con el folderId
     */
    public Mono<String> getFolderId() {
        return googleDriveCredentialsRepository.findById(CREDENTIALS_ID)
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
        return googleDriveCredentialsRepository.findById(CREDENTIALS_ID)
                .flatMap(existingCredentials -> {
                    existingCredentials.setCredentialsJson(credentials.getCredentialsJson());
                    existingCredentials.setFolderId(credentials.getFolderId());
                    return googleDriveCredentialsRepository.save(existingCredentials);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Google Drive credentials not found for update")));
    }
}
