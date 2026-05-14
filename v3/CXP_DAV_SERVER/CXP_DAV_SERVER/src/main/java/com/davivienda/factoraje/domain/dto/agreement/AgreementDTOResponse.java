package com.davivienda.factoraje.domain.dto.agreement;

import java.util.UUID;

import com.davivienda.factoraje.domain.model.DocumentModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgreementDTOResponse {

    private UUID agreement_Id;
    private String identifier;
    private String name;
    private UUID payer;
    private UUID supplier;
    private DocumentModel document;
    
}
