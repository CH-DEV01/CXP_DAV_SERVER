package com.davivienda.factoraje.domain.dto.acceptanceAudit;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcceptanceAuditDTOResponse {

    private UUID Id;
    private UUID userId;
    private String ipAddress;
    private String userAgent;
    private UUID versionTermId;
    
}
