package com.davivienda.factoraje.domain.dto.audit;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AcceptanceRequestDTO {

    @NotNull(message = "El ID de la versión no puede ser nulo")
    private UUID versionId;
}