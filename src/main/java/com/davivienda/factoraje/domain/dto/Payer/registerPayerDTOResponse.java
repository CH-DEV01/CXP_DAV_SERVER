package com.davivienda.factoraje.domain.dto.Payer;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class registerPayerDTOResponse {
    
    private UUID id;
    private String email;
    private String name;

}
