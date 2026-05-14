package com.davivienda.factoraje.domain.dto.Users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAdminRequestDTO {

    private String name;
    private String email;
    private String password;
    
}
