package com.davivienda.factoraje.domain.dto.Payer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class registerPayerDTORequest {

    private String email;
    private String name;
    private String code;
    private String nit;
    private String accountBank;
}
