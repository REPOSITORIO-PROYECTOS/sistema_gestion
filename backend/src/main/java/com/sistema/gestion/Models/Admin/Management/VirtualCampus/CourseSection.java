package com.sistema.gestion.Models.Admin.Management.VirtualCampus;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseSection {
    @Id
    private String id;

    @NotNull(message = "El campo 'nombre' no puede estar vacío")
    @Size(min = 3, max = 100, message = "El campo 'nombre' debe tener entre 3 y 100 caracteres.")
    private String name;

    private String description;

    private String createdAt;

    private String updatedAt;

    private String courseId;

    private ArrayList<String> subSectionsIds;
}
