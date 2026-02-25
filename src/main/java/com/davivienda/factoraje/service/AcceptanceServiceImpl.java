package com.davivienda.factoraje.service;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.davivienda.factoraje.domain.dto.audit.AcceptanceRequestDTO;
import com.davivienda.factoraje.domain.dto.audit.AcceptanceResponseDTO;
import com.davivienda.factoraje.domain.model.AcceptanceAuditModel;
import com.davivienda.factoraje.domain.model.TermVersionModel;
import com.davivienda.factoraje.domain.model.UserModel;
import com.davivienda.factoraje.exception.ResourceNotFoundException;
import com.davivienda.factoraje.repository.AcceptanceAuditRepository;
import com.davivienda.factoraje.repository.TermVersionRepository;
import com.davivienda.factoraje.repository.UserRepository;

@Service
public class AcceptanceServiceImpl implements AcceptanceService {

    private final AcceptanceAuditRepository auditRepository;
    
    private final TermVersionRepository versionRepository;
    
    private final UserRepository userRepository; 

    public AcceptanceServiceImpl(
        AcceptanceAuditRepository acceptanceAuditRepository, 
        TermVersionRepository termVersionRepository,
        UserRepository userRepository){

            this.auditRepository = acceptanceAuditRepository;
            this.versionRepository = termVersionRepository;
            this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public AcceptanceResponseDTO acceptSpecificTerm(UUID userId, AcceptanceRequestDTO requestDTO, HttpServletRequest httpRequest) {
        
        UUID versionId = requestDTO.getVersionId();
        if (versionId == null) {
            throw new IllegalArgumentException("El 'versionId' no puede ser nulo.");
        }

        UserModel user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        TermVersionModel versionToAccept = versionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Versión de términos no encontrada"));

        if (Boolean.FALSE.equals(versionToAccept.getIsActive())) {
            throw new IllegalArgumentException("No se puede aceptar una versión de términos que no está activa.");
        }

        if (auditRepository.existsByUserIdAndVersionId(userId, versionId)) {
            throw new IllegalStateException("El usuario ya ha aceptado esta versión de términos.");
        }

        String ipAddress = getIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        
        AcceptanceAuditModel newAudit = new AcceptanceAuditModel();
        newAudit.setUser(user);
        newAudit.setVersion(versionToAccept);
        newAudit.setIpAddress(ipAddress);
        newAudit.setUserAgent(userAgent);
        newAudit.setAcceptanceTimestamp(Instant.now());
        
        AcceptanceAuditModel savedAudit = auditRepository.save(newAudit);
        
        return new AcceptanceResponseDTO(savedAudit);
    }

    @Override
    public List<AcceptanceResponseDTO> getAcceptanceHistory(UUID userId) {
        return auditRepository.findByUserIdOrderByAcceptanceTimestampDesc(userId).stream()
                .map(AcceptanceResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.split(",")[0].trim();
    }
}