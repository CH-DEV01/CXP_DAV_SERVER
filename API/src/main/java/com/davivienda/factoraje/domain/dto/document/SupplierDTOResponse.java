package com.davivienda.factoraje.domain.dto.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierDTOResponse {
    
    private String supplierId;
    private String supplierName;
    private String supplierCode;

}
