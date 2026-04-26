package com.davivienda.factoraje.domain.dto.disbursement;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisbursementDTOResponse {
    private LocalDate disbursementDate;
}
