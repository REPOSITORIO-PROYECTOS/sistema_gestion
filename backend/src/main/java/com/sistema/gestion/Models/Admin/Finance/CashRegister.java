package com.sistema.gestion.Models.Admin.Finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
@Document(collection = "cash_registers")
public class CashRegister {
  @Id
  private String id;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm")
  private LocalDateTime startDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm")
  private LocalDateTime endDate;

  private BigDecimal totalIncome; // Total de dinero ingresado

  private BigDecimal totalExpense; // Total de dinero egresado

  private String createdBy;

  private String closedBy;

  private Boolean isClosed;
}
