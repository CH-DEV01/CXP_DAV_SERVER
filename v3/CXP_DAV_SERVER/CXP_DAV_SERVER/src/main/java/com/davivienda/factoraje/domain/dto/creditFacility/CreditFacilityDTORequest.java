package com.davivienda.factoraje.domain.dto.creditFacility;

import com.davivienda.factoraje.domain.dto.creditFacility.request.CreditFacilitySearchRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditFacilityDTORequest {

    CreditFacilitySearchRequest creditFacilitySearchRequest;
}
