package com.sistema.gestion.DTO;

import java.util.Set;

import lombok.Data;

@Data
public class UserCredentialsDTO {
	private String token;
	private String username;
	private String name;
	private Set<String> role;
}