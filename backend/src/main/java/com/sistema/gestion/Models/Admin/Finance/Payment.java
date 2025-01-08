package com.sistema.gestion.Models.Admin.Finance;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sistema.gestion.Models.ModelClass;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payment extends ModelClass {
    @Id
    private String id;

    @NotBlank
    @NotNull
    private String curseId;

    @NotBlank
    @NotNull
    private String StudentId;

    @Min(value = 0, message = "El valor del pago debe ser 0 o mayor.")
    private Double paymentAmount;

    @Min(value = 0, message = "El valor pagado debe ser 0 o mayor.")
    @Positive
    private Double PaidAmount;

    private Boolean hasDebt;

    private Boolean isPaid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDateTime paymentDueDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDateTime lastPaymentDate;
}
