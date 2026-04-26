package com.davivienda.factoraje.domain.dto.document;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponseDTO {
    
    private UUID id;
    private UUID documentId;
    private String documentNumber;
    private BigDecimal amount;
    private BigDecimal amountToFinance;
    private BigDecimal commission;
    private String issueDate;
    private String supplierName;
    private String status;
    private String disbursementDate;

}
