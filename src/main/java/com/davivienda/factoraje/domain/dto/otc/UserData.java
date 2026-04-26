package com.davivienda.factoraje.domain.dto.otc;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserData {

    private String app;
    private String otc;
    private String user;
    private String userName;
    private String userEmail;
    private String companyName;
    private String companyUserNIU;
    private String status;
    private String timestamp;
}