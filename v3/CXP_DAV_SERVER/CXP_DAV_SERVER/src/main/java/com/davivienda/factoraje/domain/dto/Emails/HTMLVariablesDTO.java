package com.davivienda.factoraje.domain.dto.Emails;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HTMLVariablesDTO {
    
    private String nombreEmpresa;
    private String numeroLineaCredito;
    private String nombreProveedor;
    private String numeroCuentaProveedor;
    private String NIT;
    private BigDecimal montoDesembolsar;
    private BigDecimal comission;
    private String distrito;
    private String municipio;
    private String departamento;
    private String numeroCuentaPagador;
}
