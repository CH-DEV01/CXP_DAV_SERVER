package com.davivienda.factoraje.domain.dto.creditFacility.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmountDetail {
    private BigDecimal amount;
    private String currency;
}
