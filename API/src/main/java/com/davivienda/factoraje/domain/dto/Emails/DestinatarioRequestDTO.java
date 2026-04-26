package com.davivienda.factoraje.domain.dto.Emails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DestinatarioRequestDTO {
    
    private String email;
    private String name;
}
