package com.davivienda.factoraje.domain.dto.Permissions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionRequestDTO {

    private String permissionName;
    private String permissionDescription;
}
