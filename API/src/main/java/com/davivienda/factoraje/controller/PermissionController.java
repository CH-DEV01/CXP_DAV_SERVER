package com.davivienda.factoraje.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.davivienda.factoraje.auth.RolesAllowed;
import com.davivienda.factoraje.domain.dto.Permissions.PermissionRequestDTO;
import com.davivienda.factoraje.domain.dto.Permissions.PermissionResponseDTO;
import com.davivienda.factoraje.service.PermissionService;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private PermissionService permissionService;
    private static final Logger log = LoggerFactory.getLogger(PermissionController.class);

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
        log.info("PermissionController initialized");
    }

    @RolesAllowed("MANAGER")
    @PostMapping("/create")
    public ResponseEntity<?> createPermission(@RequestBody PermissionRequestDTO permissionRequest) {
        log.info("POST /api/permissions/create - create permission: {}", permissionRequest);
        if (permissionRequest == null || permissionRequest.getPermissionName() == null) {
            log.warn("PermissionRequestDTO is null or missing required fields");
            return ResponseEntity
                    .badRequest()
                    .body("El cuerpo de la petición no puede estar vacío y debe incluir 'permissionName'");
        }
        PermissionResponseDTO created = permissionService.create(permissionRequest);
        log.info("Permission created with id={}", created.getPermission_id());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(created);
    }

}
