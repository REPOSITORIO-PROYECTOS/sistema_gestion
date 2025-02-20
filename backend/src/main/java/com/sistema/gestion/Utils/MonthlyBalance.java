package com.sistema.gestion.Utils;

import java.math.BigDecimal;
import java.util.List;

import com.sistema.gestion.Models.Admin.Finance.CashRegister;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyBalance {
	private List<CashRegister> dailyBalances;
	private BigDecimal totalIncome;
	private BigDecimal totalExpense;
}