package com.davivienda.factoraje.domain.dto.termVersion;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TermVersionDTOResponse {

    private UUID Id;
    private UUID termTypeId;
    private String legalText;
    private String versionNumber;
    private Boolean isActive;
}