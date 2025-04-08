package com.sistema.gestion.Models.Admin.Management.VirtualCampus;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class News {

    @Id
    private String id;

    @NotBlank(message = "Categoría requerida")
    private String category;

    @NotBlank(message = "Autor requerido")
    private String author;

    @NotBlank(message = "Título requerido")
    private String title;

    @NotBlank(message = "Contenido requerido")
    private String content;
    
    @NotBlank(message = "Fecha requerida")
    private String date;

    private boolean isImportant;
}
