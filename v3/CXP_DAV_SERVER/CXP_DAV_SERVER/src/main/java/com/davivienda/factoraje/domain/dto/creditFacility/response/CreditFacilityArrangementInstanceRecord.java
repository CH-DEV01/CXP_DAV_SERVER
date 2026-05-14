package com.davivienda.factoraje.domain.dto.creditFacility.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditFacilityArrangementInstanceRecord {
    private String productInstanceReference;
    private CustomerReference customerReference;
    private List<CreditFacility> creditFacilities;
}
