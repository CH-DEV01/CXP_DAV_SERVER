package com.davivienda.factoraje.domain.dto.Emails;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequestDTO {

    private List<DestinatarioRequestDTO> destinatarios;
    private int tipoHtml; 
    private HTMLVariablesDTO htmlVariables;

}
