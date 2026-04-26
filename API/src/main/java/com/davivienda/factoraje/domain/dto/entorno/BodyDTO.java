package com.davivienda.factoraje.domain.dto.entorno;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class BodyDTO {

    @JacksonXmlProperty(localName = "contenedor")
    private String contenedor;
    
}
