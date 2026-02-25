package com.davivienda.factoraje.domain.dto.Roles;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequestDTO {

    private String roleName;
    private String roleDescription; 
}
