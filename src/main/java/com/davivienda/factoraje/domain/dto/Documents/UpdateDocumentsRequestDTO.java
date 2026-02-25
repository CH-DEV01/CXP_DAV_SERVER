package com.davivienda.factoraje.domain.dto.Documents;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDocumentsRequestDTO {
    
    private List<UUID> documentIds;
    private BigDecimal toFinanceAmount;
    private BigDecimal comission;
}
