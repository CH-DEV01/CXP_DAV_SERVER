package com.davivienda.factoraje.domain.dto.disbursement;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisbursementDTORequest {
    private UUID entityId;
    private LocalDate today;
}
