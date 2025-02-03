package com.sistema.gestion.Models.Admin.Management;

import java.util.Set;

import org.springframework.data.annotation.Id;

import com.sistema.gestion.Models.ModelClass;
import com.sistema.gestion.Utils.CourseStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Course extends ModelClass {
    @Id
    private String id;

    @NotNull(message = "El título del curso no puede ser nulo.")
    @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres.")
    private String title;

    @NotNull(message = "La descripción del curso no puede ser nula.")
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres.")
    private String description;

    @NotNull(message = "El estado del curso es obligatorio.")
    private CourseStatus status;

    @NotNull(message = "El precio mensual no puede ser nulo.")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio mensual debe ser mayor que cero.")
    private Double monthlyPrice;

    private Set<String> studentsIds;

    @NotNull(message = "El ID del profesor no puede ser nulo.")
    private String teacherId;
}