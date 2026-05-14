package com.davivienda.factoraje.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davivienda.factoraje.domain.dto.acceptanceAudit.AcceptanceAuditDTORequest;
import com.davivienda.factoraje.domain.dto.acceptanceAudit.AcceptanceAuditDTOResponse;
import com.davivienda.factoraje.service.AcceptanceAuditService;

@RestController
@RequestMapping("/api/terms")
public class AcceptanceAuditController {

    private final AcceptanceAuditService acceptanceAuditService;
    private static final Logger log = LoggerFactory.getLogger(RoleController.class);

    public AcceptanceAuditController(AcceptanceAuditService acceptanceAuditService){

        this.acceptanceAuditService = acceptanceAuditService;
        log.info("AcceptanceAuditController initialized");
    }

    // CREATE
    @PostMapping
    public ResponseEntity<AcceptanceAuditDTOResponse> create(@RequestBody AcceptanceAuditDTORequest request) {

        AcceptanceAuditDTOResponse response = acceptanceAuditService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
