package com.davivienda.factoraje.domain.dto.Users;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignRolesToUserRequestDTO {
    
    private UUID userId;
    private List<UUID> roleIds;
}
