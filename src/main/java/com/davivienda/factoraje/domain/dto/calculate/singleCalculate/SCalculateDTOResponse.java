package com.davivienda.factoraje.domain.dto.calculate.singleCalculate;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SCalculateDTOResponse {

    private BigDecimal amountToFinance;
    private BigDecimal comission;
    
}
