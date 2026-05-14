package com.davivienda.factoraje.domain.dto.creditFacility.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerReference {

    private String partyReferenceType;
    private String partyReferenceValue;
    
}
