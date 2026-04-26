package com.davivienda.factoraje.domain.dto.entorno;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "respuestaEntorno")
public class RespuestaEntornoDTO {

    @JacksonXmlProperty(localName = "header")
    private HeaderDTO header;
    
    @JacksonXmlProperty(localName = "body")
    private BodyDTO body;

    public HeaderDTO getHeader() {
        return header;
    }

    public void setHeader(HeaderDTO header) {
        this.header = header;
    }

    public BodyDTO getBody() {
        return body;
    }

    public void setBody(BodyDTO body) {
        this.body = body;
    }
}
