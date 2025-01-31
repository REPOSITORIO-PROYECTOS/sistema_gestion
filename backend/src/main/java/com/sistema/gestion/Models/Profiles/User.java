package com.sistema.gestion.Models.Profiles;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import com.sistema.gestion.Models.ModelClass;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends ModelClass {
  @Id
  private String id;

  @NotBlank(message = "El nombre no puede estar en blanco.")
  @Size(min = 3, max = 20, message = "El nombre debe contener entre 3 a 20 caracteres.")
  private String name;

  @NotBlank(message = "El apellido no puede estar en blanco.")
  @Size(min = 3, max = 20, message = "El apellido debe contener entre 3 a 20 caracteres.")
  private String surname;

  @NotBlank(message = "El DNI no puede estar en blanco.")
  @Indexed(unique = true)
  private String dni;

  private String phone;

  // TODO: Idear Relacion unica con la institucion
  private String istitution;

  @NotBlank(message = "El correo electrónico no puede estar vacío")
  @Email(message = "Debe proporcionar un correo electrónico válido")
  @Indexed(unique = true)
  private String email;

  @NotBlank(message = "La contraseña no puede estar vacía")
  private String password;

  @NotBlank(message = "El rol no puede estar en blanco.")
  private Set<String> roles = new HashSet<>();
}
