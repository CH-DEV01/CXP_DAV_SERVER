package com.davivienda.factoraje.domain.dto.paymentPolicy;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPolicyRequestDTO {

    // Fecha de emision del documento
    private UUID entityId;
    private String issueDate;
}
