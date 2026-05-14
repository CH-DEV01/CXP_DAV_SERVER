package com.davivienda.factoraje.domain.dto.paymentPolicy;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPolicyResponseDTO {

    private String documentNumber;
    private Long passedDays;
    private LocalDate issueDate;
    private LocalDate todayDate;
    private Boolean result;
    private Integer policy;
    
}
