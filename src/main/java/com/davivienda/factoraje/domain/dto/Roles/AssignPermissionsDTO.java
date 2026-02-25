package com.davivienda.factoraje.domain.dto.Roles;

import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignPermissionsDTO {
    
    private UUID role_id; 
    private Set<UUID> permissions;

}
