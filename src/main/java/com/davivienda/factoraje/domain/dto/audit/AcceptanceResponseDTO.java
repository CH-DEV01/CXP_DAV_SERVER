package com.davivienda.factoraje.domain.dto.audit;

import com.davivienda.factoraje.domain.model.AcceptanceAuditModel;
import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class AcceptanceResponseDTO {
    private UUID acceptanceId;
    private Instant acceptedAt;
    private String documentName;
    private String versionNumber;
    private UUID versionId;

    public AcceptanceResponseDTO(AcceptanceAuditModel audit) {
        this.acceptanceId = audit.getId();
        this.acceptedAt = audit.getAcceptanceTimestamp();
        this.versionId = audit.getVersion().getId();
        this.versionNumber = audit.getVersion().getVersionNumber();
        this.documentName = audit.getVersion().getTermType().getDocumentName();
    }
}