package com.davivienda.factoraje.domain.dto.Users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginAdminRequestDTO {

    private String email;
    private String password;
    
}
