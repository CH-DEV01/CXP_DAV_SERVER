package com.davivienda.factoraje.domain.dto.calculate;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PercentageDTOResponse {
    
    private BigDecimal percentage;
}
