package com.davivienda.factoraje.domain.dto.Auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeticionEntornoRequest {
    private String fabrica;
    private String servicio;
    private String usuario;
    private String clave;
    private String tipoPersona;
}