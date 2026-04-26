package com.davivienda.factoraje.domain.dto.parameter;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ParameterDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class request {

        private String param_key;
        private String param_value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class response {

        private UUID id;
        private String param_key;
        private String param_value;
        private String description;
        private String category;
    }
    
}
