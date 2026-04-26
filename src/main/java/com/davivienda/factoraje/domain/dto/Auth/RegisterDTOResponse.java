package com.davivienda.factoraje.domain.dto.Auth;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTOResponse {

    private UUID id;
    private String name;
    private String email;
    private String dui;
    private UUID entityId;
    private String entityName;
}
