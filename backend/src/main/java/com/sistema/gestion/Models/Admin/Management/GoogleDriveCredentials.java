package com.sistema.gestion.Models.Admin.Management;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleDriveCredentials {
    @Id
    private String id;
    private String folderId;
    private String credentialsJson;
}
