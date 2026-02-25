package com.davivienda.factoraje.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.davivienda.factoraje.auth.PermissionsAllowed;
import com.davivienda.factoraje.auth.RolesAllowed;
import com.davivienda.factoraje.domain.model.EntityModel;
import com.davivienda.factoraje.service.EntityService;

@RestController
@RequestMapping("/api/entities")
public class EntityController {

    private static final Logger log = LoggerFactory.getLogger(EntityController.class);
    private final EntityService entityService;

    public EntityController(EntityService entityService) {
        this.entityService = entityService;
        log.info("EntityController initialized");
    }

    @RolesAllowed({ "MANAGER" })
    @PermissionsAllowed({ "create_payer_execute" })
    @PostMapping("/create")
    public ResponseEntity<?> createEntity(@RequestBody EntityModel entity) {
        log.info("POST /api/entities/create - create entity: {}", entity);
        if (entity == null) {
            log.warn("Entity model is null");
            return ResponseEntity
                .badRequest()
                .body("El cuerpo de la petición no puede ser vacío");
        }
        EntityModel created = entityService.createEntity(entity);
        log.info("Entity created with id={}", created.getId());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(created);
    }
    
    @RolesAllowed({ "MANAGER, AUTHORIZING" })
    @PermissionsAllowed({ "payer_management_view" })
    @GetMapping("/all")
    public ResponseEntity<?> getEntities() {
        log.info("GET /api/entities/all - get all entities");
        List<EntityModel> list = entityService.getEntities();
        log.info("Found {} entities", list.size());
        return ResponseEntity.ok(list);
    }

    @RolesAllowed({"SUPPLIER", "SUPPLIER_TWO_MODE_AUTH"})
    @PermissionsAllowed({ "select_documents_execute_two_mode_auth", "select_documents_execute" })
    @GetMapping("/{id}")
    public ResponseEntity<?> getEntityById(@PathVariable UUID id) {
        log.info("GET /api/entities/{} - get entity by id", id);
        if (id == null) {
            log.warn("ID is null");
            return ResponseEntity
                .badRequest()
                .body("El parámetro 'id' no puede ser nulo");
        }

        EntityModel entity = entityService.getEntityById(id);
        if (entity == null) {
            log.info("Entity not found for id={}", id);
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Entidad no encontrada");
        }
        log.info("Entity found for id={}", id);
        return ResponseEntity.ok(entity);
    }

    @RolesAllowed({ "MANAGER", "AUTHORIZING_TWO_MODE_AUTH", "AUTHORIZING" })
    @PermissionsAllowed({ "payer_management_view", "approve_documents_two_mode_auth_execute" })
    @GetMapping("/type")
    public ResponseEntity<?> getEntitiesByType(@RequestParam boolean isEntityType) {
        log.info("GET /api/entities/type?isEntityType={}", isEntityType);
        List<EntityModel> list = entityService.getEntitiesByType(isEntityType);
        log.info("Found {} entities for type {}", list.size(), isEntityType);
        return ResponseEntity.ok(list);
    }
}
