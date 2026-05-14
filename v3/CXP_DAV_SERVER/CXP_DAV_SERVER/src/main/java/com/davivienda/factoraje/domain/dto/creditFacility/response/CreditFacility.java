package com.davivienda.factoraje.domain.dto.creditFacility.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditFacility {
    private String facilityType;
    private AmountDetail facilityAmount;
    private List<Position> position;
    private DateType dateType;
    private List<Drawdown> drawdowns;
}