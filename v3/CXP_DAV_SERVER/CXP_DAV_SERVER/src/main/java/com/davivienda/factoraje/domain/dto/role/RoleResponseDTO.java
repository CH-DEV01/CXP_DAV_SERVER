package com.davivienda.factoraje.domain.dto.role;

import java.util.Set;
import java.util.UUID;

import com.davivienda.factoraje.domain.model.PermissionModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponseDTO {
    
    private UUID role_id;
    private String roleName;
    private String roleDescription;
    private Set<PermissionModel> permissions;
}
