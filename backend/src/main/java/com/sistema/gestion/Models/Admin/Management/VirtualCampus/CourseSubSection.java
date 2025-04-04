package com.sistema.gestion.Models.Admin.Management.VirtualCampus;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseSubSection {
    @Id
    private String id;

    @NotNull(message = "El campo 'body' no puede estar vacío")
    private String body;

    private String SectionId;

    private ArrayList<String> filesIds;

    private ArrayList<String> imagesIds;
}
