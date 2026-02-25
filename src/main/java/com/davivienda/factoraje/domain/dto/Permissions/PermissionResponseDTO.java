package com.davivienda.factoraje.domain.dto.Permissions;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponseDTO {

    private UUID permission_id;
    private String permissionName;
    private String permissionDescription;
}
