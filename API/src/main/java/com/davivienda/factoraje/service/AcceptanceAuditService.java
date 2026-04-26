package com.davivienda.factoraje.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.davivienda.factoraje.controller.RoleController;
import com.davivienda.factoraje.domain.dto.acceptanceAudit.AcceptanceAuditDTORequest;
import com.davivienda.factoraje.domain.dto.acceptanceAudit.AcceptanceAuditDTOResponse;
import com.davivienda.factoraje.domain.dto.parameter.ParameterDTO;
import com.davivienda.factoraje.domain.model.AcceptanceAuditModel;
import com.davivienda.factoraje.domain.model.TermVersionModel;
import com.davivienda.factoraje.domain.model.UserModel;
import com.davivienda.factoraje.repository.AcceptanceAuditRepository;

@Service
public class AcceptanceAuditService {

    private final AcceptanceAuditRepository acceptanceAuditRepository;
    private final UserService userService;
    private final TermVersionService termVersionService;
    private static final Logger log = LoggerFactory.getLogger(RoleController.class);

    public AcceptanceAuditService(AcceptanceAuditRepository acceptanceAuditRepository, UserService userService, TermVersionService termVersionService){
        this.acceptanceAuditRepository = acceptanceAuditRepository;
        this.userService = userService;
        this.termVersionService = termVersionService;
        log.info("AcceptanceAuditService initialized");
    }

    // CREATE
    @Transactional
    public AcceptanceAuditDTOResponse create(AcceptanceAuditDTORequest dto){

        AcceptanceAuditModel acceptanceAuditToSave = new AcceptanceAuditModel();
        UserModel user = userService.getUserById(dto.getUserId());
        TermVersionModel termVersion = termVersionService.findByIdEntity(dto.getVersionTermId());
        
        acceptanceAuditToSave.setAcceptanceTimestamp(Instant.now());
        acceptanceAuditToSave.setIpAddress(dto.getIpAddress());
        acceptanceAuditToSave.setUser(user);
        acceptanceAuditToSave.setUserAgent(dto.getUserAgent());
        acceptanceAuditToSave.setVersion(termVersion);

        AcceptanceAuditModel acceptanceAuditSaved = acceptanceAuditRepository.save(acceptanceAuditToSave);

        AcceptanceAuditDTOResponse response = new AcceptanceAuditDTOResponse();
        
        response.setId(acceptanceAuditSaved.getId());
        response.setIpAddress(acceptanceAuditSaved.getIpAddress());
        response.setUserAgent(acceptanceAuditSaved.getUserAgent());
        response.setUserId(acceptanceAuditSaved.getUser().getId());
        response.setVersionTermId(acceptanceAuditSaved.getVersion().getId());
        
        return response;

    }

}