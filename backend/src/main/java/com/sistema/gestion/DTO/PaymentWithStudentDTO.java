package com.sistema.gestion.DTO;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sistema.gestion.Utils.PaymentType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentWithStudentDTO {
  private String id;
  private String studentId;
  private String studentName;
  private String curseId;
  private String curseName;
  private Double paymentAmount;
  private Double paidAmount;
  private Boolean hasDebt;
  private Boolean isPaid;

  private PaymentType paymentType;

  private String modifiedBy;

  private String createdBy;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
  private LocalDateTime paymentDueDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
  private LocalDateTime lastPaymentDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm")
  private LocalDateTime createdAt;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm")
  private LocalDateTime updatedAt;
}