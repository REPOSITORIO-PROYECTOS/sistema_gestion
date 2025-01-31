package com.sistema.gestion.DTO;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class UserDTO {
  private String id;

  private String name;

  private String surname;

  private String dni;

  private String phone;

  // TODO: Idear Relacion unica con la institucion
  private String istitution;

  private String email;

  private Set<String> roles = new HashSet<>();

}
