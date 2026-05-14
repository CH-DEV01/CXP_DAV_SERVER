package com.davivienda.factoraje.domain.dto.creditFacility;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditFacilityInternalDTOResponse {
    
    private String creditLineNumber;
    private String availablePercentage;
}