package com.sistema.gestion.DTO;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
	@NotBlank(message = "El nombre de usuario no puede estar vacío")
	private String dni;
	@NotBlank(message = "La contraseña no puede estar vacía")
	private String password;

	// Getters and setters
	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}