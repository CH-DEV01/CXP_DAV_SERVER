package com.davivienda.factoraje.domain.dto.calculate;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculateDTORequest {

    private String issueDate;
    private String cutOffDate;
    private String documentNumber;
    private Integer diffDays;
    private BigDecimal amount;
    private String documentID;

}
