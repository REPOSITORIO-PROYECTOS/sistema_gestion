package com.sistema.gestion.Models.Admin.Finance;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sistema.gestion.Models.ModelClass;
import com.sistema.gestion.Utils.PaymentType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payment extends ModelClass {
    @Id
    private String id;

    @NotBlank
    @NotNull
    private String courseId;

    @NotBlank
    @NotNull
    private String studentId;

    @Min(value = 0, message = "El valor del pago debe ser 0 o mayor.")
    private Double paymentAmount;

    @Min(value = 0, message = "El valor pagado debe ser 0 o mayor.")
    private Double paidAmount;

    private Boolean hasDebt;

    private Boolean isPaid;

    private PaymentType paymentType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate paymentDueDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm")
    private LocalDateTime lastPaymentDate;
}
