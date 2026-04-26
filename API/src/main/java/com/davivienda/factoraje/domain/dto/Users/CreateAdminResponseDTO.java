package com.davivienda.factoraje.domain.dto.Users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAdminResponseDTO {
    
    private String name;
    private String email;

}
