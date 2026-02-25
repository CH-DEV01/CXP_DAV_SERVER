package com.davivienda.factoraje.controller;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.davivienda.factoraje.auth.RolesAllowed;
import com.davivienda.factoraje.domain.dto.Roles.RoleResponseDTO;
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

    @RolesAllowed("MANAGER")
    @GetMapping("/get-roles")
    public ResponseEntity<?> getAllRoles() {
        log.info("GET /api/roles/get-roles - fetch all roles");
        Set<RoleResponseDTO> roles = roleService.getAll();
        log.info("Found {} roles", roles.size());
        return ResponseEntity.ok(roles); 
    }
}
