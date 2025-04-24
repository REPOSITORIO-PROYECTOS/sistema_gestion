package com.sistema.gestion.Models.Admin.Management;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.sistema.gestion.Models.ModelClass;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Document(collection = "grades")
public class Grade extends ModelClass {

    @Id
    private String id;

    @NotNull(message = "El estudiante es obligatorio.")
    @Indexed
    private String studentId; // Referencia al estudiante

    @NotNull(message = "El curso es obligatorio.")
    @Indexed
    private String courseId; // Referencia al curso

    @NotNull(message = "La sección es obligatoria.")
    @Indexed
    private String sectionId;

    @NotNull(message = "La calificación es obligatoria.")
    @Min(value = 0, message = "La calificación mínima es 0.")
    @Max(value = 10, message = "La calificación máxima es 10.")
    private Double grade;

    @NotNull(message = "La fecha de evaluación es obligatoria.")
    private LocalDate evaluationDate;

    private String comments;

}

