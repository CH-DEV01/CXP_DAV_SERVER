package com.davivienda.factoraje.controller;

import java.util.Set;
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
import com.davivienda.factoraje.auth.RolesAllowed;
import com.davivienda.factoraje.domain.dto.role.RoleRequestDTO;
import com.davivienda.factoraje.domain.dto.role.RoleResponseDTO;
import com.davivienda.factoraje.service.RoleService;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private static final Logger log = LoggerFactory.getLogger(RoleController.class);
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
        log.info("RoleController initialized");
    }

    // CREATE
    @PostMapping()
    public ResponseEntity<RoleResponseDTO> create(@RequestBody RoleRequestDTO request) {

        RoleResponseDTO response = roleService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // READ ALL
    @RolesAllowed({"MANAGER"})
    @GetMapping
    public ResponseEntity<Set<RoleResponseDTO>> getAll() {
        return ResponseEntity.ok().body(roleService.getAllRoles());
    }

    // READ ONE 
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> getById(@PathVariable UUID id) {
        RoleResponseDTO response = roleService.findById(id);
        return ResponseEntity.ok(response);
    }

    // UPDATE 
    @PutMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> update(
            @PathVariable UUID id, 
            @RequestBody RoleRequestDTO request) {
        RoleResponseDTO response = roleService.update(id, request);
        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build(); 
    }

}
