package com.davivienda.factoraje.domain.dto.calculate;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailDTOResponse {

    private String issueDate; // enviar
    private String cutOffDate;
    private Integer financingDays; // enviar
    private String documentNumber;
    private BigDecimal amount;
    private BigDecimal amountToFinance;
    private BigDecimal interests;
    private BigDecimal commissions;
    private BigDecimal amountToBeDisbursed;
}
