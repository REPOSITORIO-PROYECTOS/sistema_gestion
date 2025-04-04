package com.sistema.gestion.Models.Admin.Management.VirtualCampus;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class File {
    @Id
    private String id;

    @NotNull(message = "El campo 'nombre de archivo' no puede estar vacío")
    @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres.")
    private String name;

    @NotNull(message = "El campo 'enlace a archivo' no puede estar vacío")
    private String link;

    private String subSectionId;

    public File() {
    }

    public File(String name,String link) {
        this.name = name;
        this.link = link;
    }
}
