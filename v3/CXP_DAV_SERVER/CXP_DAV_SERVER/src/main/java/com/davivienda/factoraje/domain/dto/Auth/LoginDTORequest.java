package com.davivienda.factoraje.domain.dto.Auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTORequest {

    private String dui;
    private String password;

}
