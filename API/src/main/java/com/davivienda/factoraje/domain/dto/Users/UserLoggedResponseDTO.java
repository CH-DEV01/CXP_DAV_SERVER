package com.davivienda.factoraje.domain.dto.Users;

import java.util.UUID;

import com.davivienda.factoraje.domain.model.RoleModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoggedResponseDTO {
    
    private UUID id;
    private String name;
    private String email;
    private RoleModel role;
    private String dui;
    private Boolean entityType;
    private UUID entityId;
    private String entityName;
    private Boolean authenticationMode;
}
