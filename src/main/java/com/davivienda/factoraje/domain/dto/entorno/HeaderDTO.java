package com.davivienda.factoraje.domain.dto.entorno;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class HeaderDTO {

    @JacksonXmlProperty(localName = "codigo")
    private String codigo;

    @JacksonXmlProperty(localName = "descripcion")
    private String descripcion;

    // Getters y Setters
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
