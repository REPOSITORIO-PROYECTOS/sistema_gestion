package com.sistema.gestion.Models.Profiles;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sistema.gestion.Models.ModelClass;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Teacher extends ModelClass{
    @Id
    private String id;

    @NotBlank(message = "El nombre no puede estar en blanco.")
    @Size(min = 3, max = 20, message = "El nombre debe contener entre 3 a 20 caracteres.")
    private String name;

    @NotBlank(message = "El apellido no puede estar en blanco.")
    @Size(min = 3, max = 20, message = "El apellido debe contener entre 3 a 20 caracteres.")
    private String surname;

    @Email(message = "Ingrese un mail válido.")
    @Indexed(unique = true)
    private String email;

    @Indexed(unique = true)
    private String dni;

    private String phone;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dateOfBirth;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate ingressDate;

    private Set<String> coursesIds;
}
