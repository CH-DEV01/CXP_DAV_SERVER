package com.davivienda.factoraje.domain.dto.agreement;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkUpdateRequestDTO {

    private List<ApprovalRequestDTO> approvals;
    private String status;
    private UUID payerId;
    private Integer authMode;
    private UUID userId;
    
}
