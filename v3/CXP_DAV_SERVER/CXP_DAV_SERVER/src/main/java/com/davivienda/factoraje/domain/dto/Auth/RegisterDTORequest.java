package com.davivienda.factoraje.domain.dto.Auth;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTORequest {

    private String email;
    private String name;
    private String dui;
    private UUID entityId;
    private UUID roleId;
}
