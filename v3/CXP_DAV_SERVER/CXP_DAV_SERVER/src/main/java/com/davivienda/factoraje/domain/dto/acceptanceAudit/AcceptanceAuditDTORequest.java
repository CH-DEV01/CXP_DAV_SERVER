package com.davivienda.factoraje.domain.dto.acceptanceAudit;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcceptanceAuditDTORequest {
    
    private String ipAddress;
    private String userAgent;
    private UUID userId;
    private UUID versionTermId;
}
