package com.davivienda.factoraje.domain.dto.termType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TermTypeDTORequest {

    private String documentName;
    private String unique_code;
    
}
