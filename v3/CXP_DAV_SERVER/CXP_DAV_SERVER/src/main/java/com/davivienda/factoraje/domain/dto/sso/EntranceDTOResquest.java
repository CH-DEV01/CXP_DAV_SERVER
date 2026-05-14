package com.davivienda.factoraje.domain.dto.sso;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntranceDTOResquest {
    private String sessionToken;
}
