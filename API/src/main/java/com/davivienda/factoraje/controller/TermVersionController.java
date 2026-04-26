package com.davivienda.factoraje.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davivienda.factoraje.domain.dto.termVersion.TermVersionDTORequest;
import com.davivienda.factoraje.domain.dto.termVersion.TermVersionDTOResponse;
import com.davivienda.factoraje.service.TermVersionService;

@RestController
@RequestMapping("/api/term-versions")
public class TermVersionController {

    private static final Logger log = LoggerFactory.getLogger(RoleController.class);
    private final TermVersionService termVersionService;

    public TermVersionController(TermVersionService termVersionService) {
        this.termVersionService = termVersionService;
        log.info("TermVersionController initialized");
    }

    // CREATE
    @PostMapping
    public ResponseEntity<TermVersionDTOResponse> create(@RequestBody TermVersionDTORequest request) {

        TermVersionDTOResponse response = termVersionService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<TermVersionDTOResponse>> getAll() {
        return ResponseEntity.ok().body(termVersionService.getAllTermVersions());
    }

    // READ ONE 
    @GetMapping("/{id}")
    public ResponseEntity<TermVersionDTOResponse> getById(@PathVariable UUID id) {
        TermVersionDTOResponse response = termVersionService.findById(id);
        return ResponseEntity.ok(response);
    }

    // UPDATE 
    @PutMapping("/{id}")
    public ResponseEntity<TermVersionDTOResponse> update(
            @PathVariable UUID id, 
            @RequestBody TermVersionDTORequest request) {
        TermVersionDTOResponse response = termVersionService.update(id, request);
        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        termVersionService.delete(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
    
}
