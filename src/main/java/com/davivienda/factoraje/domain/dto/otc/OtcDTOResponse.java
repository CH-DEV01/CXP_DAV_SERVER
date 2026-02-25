package com.davivienda.factoraje.domain.dto.otc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtcDTOResponse {

    private Boolean success;
    private UserData userData; 
    private String message;
}
