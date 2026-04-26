package com.davivienda.financiamiento.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartSessionResponseDTO {
    private Boolean success;
    private String message;
    private String jwt;
}
