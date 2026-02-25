package com.davivienda.factoraje.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davivienda.factoraje.domain.dto.audit.AcceptanceRequestDTO;
import com.davivienda.factoraje.domain.dto.audit.AcceptanceResponseDTO;
import com.davivienda.factoraje.service.AcceptanceService;

@RestController
@RequestMapping("/api/terms")
public class AcceptanceController {

    @Autowired
    private AcceptanceService acceptanceService;

    @PostMapping("/accept")
    public ResponseEntity<AcceptanceResponseDTO> acceptSpecificTerm(

            @Valid @RequestBody AcceptanceRequestDTO requestDTO,
            HttpServletRequest request
    ) {// --- SIMULACIÓN ---
        UUID currentUserId = UUID.fromString("tu-uuid-de-usuario-logueado"); 
        // UUID currentUserId = userDetails.getId(); 
        // --- FIN SIMULACIÓN ---

        AcceptanceResponseDTO accepted = acceptanceService.acceptSpecificTerm(
            currentUserId, 
            requestDTO, 
            request
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(accepted);
    }

    /**
     * Endpoint para VER HISTORIAL (READ). (Sin cambios)
     */
    @GetMapping("/history")
    public ResponseEntity<List<AcceptanceResponseDTO>> getMyHistory(
            // @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // --- SIMULACIÓN ---
        UUID currentUserId = UUID.fromString("tu-uuid-de-usuario-logueado");
        // --- FIN SIMULACIÓN ---
        
        List<AcceptanceResponseDTO> history = acceptanceService.getAcceptanceHistory(currentUserId);
        return ResponseEntity.ok(history);
    }
}
