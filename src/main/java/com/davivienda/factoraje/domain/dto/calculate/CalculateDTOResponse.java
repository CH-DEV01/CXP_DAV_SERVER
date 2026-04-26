package com.davivienda.factoraje.domain.dto.calculate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculateDTOResponse {

    private BigDecimal amountToFinance;
    private BigDecimal amount;
    private BigDecimal interests;
    private BigDecimal commissions;
    private BigDecimal amountToBeDisbursed;
    private List<DetailDTOResponse> detail = new ArrayList<>();
}
