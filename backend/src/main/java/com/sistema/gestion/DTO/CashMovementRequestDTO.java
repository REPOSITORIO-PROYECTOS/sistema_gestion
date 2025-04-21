package com.sistema.gestion.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CashMovementRequestDTO {
	private String PaymentId;
	private String concept;
	private double amount;
	private boolean isIncome;
}
