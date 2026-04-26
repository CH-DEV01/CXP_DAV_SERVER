package com.davivienda.factoraje.domain.dto.otc;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtcDTOResponse {

    private Boolean success;
    private UserData data; 
    private String message;
    private String timestamp;
    private String agent;
}


