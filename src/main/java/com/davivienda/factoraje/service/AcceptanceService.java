package com.davivienda.factoraje.service;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.davivienda.factoraje.domain.dto.audit.AcceptanceRequestDTO;
import com.davivienda.factoraje.domain.dto.audit.AcceptanceResponseDTO;

public interface AcceptanceService {

    AcceptanceResponseDTO acceptSpecificTerm(UUID userId, AcceptanceRequestDTO requestDTO, HttpServletRequest httpRequest);
    
    List<AcceptanceResponseDTO> getAcceptanceHistory(UUID userId);
}