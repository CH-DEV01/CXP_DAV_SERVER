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

import com.davivienda.factoraje.domain.dto.termType.TermTypeDTORequest;
import com.davivienda.factoraje.domain.dto.termType.TermTypeDTOResponse;
import com.davivienda.factoraje.service.TermTypeService;

@RestController
@RequestMapping("/api/term-types")
public class TermTypeController {

    private static final Logger log = LoggerFactory.getLogger(RoleController.class);
    private final TermTypeService termTypeService;

    public TermTypeController(TermTypeService termTypeService) {
        this.termTypeService = termTypeService;
        log.info("TermTypeController initialized");
    }

    // CREATE
    @PostMapping
    public ResponseEntity<TermTypeDTOResponse> create(@RequestBody TermTypeDTORequest request) {

        TermTypeDTOResponse response = termTypeService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<TermTypeDTOResponse>> getAll() {
        return ResponseEntity.ok().body(termTypeService.getAllTermTypes());
    }

    // READ ONE 
    @GetMapping("/{id}")
    public ResponseEntity<TermTypeDTOResponse> getById(@PathVariable UUID id) {
        TermTypeDTOResponse response = termTypeService.findById(id);
        return ResponseEntity.ok(response);
    }

    // UPDATE 
    @PutMapping("/{id}")
    public ResponseEntity<TermTypeDTOResponse> update(
            @PathVariable UUID id, 
            @RequestBody TermTypeDTORequest request) {
        TermTypeDTOResponse response = termTypeService.update(id, request);
        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        termTypeService.delete(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
    
}
