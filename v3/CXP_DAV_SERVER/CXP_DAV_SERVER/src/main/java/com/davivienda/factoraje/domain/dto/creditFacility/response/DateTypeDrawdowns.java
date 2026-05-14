package com.davivienda.factoraje.domain.dto.creditFacility.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateTypeDrawdowns {

    private String maturityDate;
    private String openingDate;
}