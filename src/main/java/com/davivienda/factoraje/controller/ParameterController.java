package com.davivienda.factoraje.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
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
import com.davivienda.factoraje.auth.PermissionsAllowed;
import com.davivienda.factoraje.auth.RolesAllowed;
import com.davivienda.factoraje.domain.dto.parameter.ParameterDTO;
import com.davivienda.factoraje.service.ParameterService;

@RestController
@RequestMapping("/parameters")
public class ParameterController {

    private final CacheManager cacheManager;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final ParameterService parameterService; 

    public ParameterController(CacheManager cacheManager, ParameterService parameterService) {
        this.cacheManager = cacheManager;
        this.parameterService = parameterService;
        log.info("ParameterController initialized");
    }

    // CREATE
    @RolesAllowed({"MANAGER"})
    @PostMapping()
    public ResponseEntity<ParameterDTO.response> create(@RequestBody ParameterDTO.request request) {

        ParameterDTO.response response = parameterService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // READ ALL
    @RolesAllowed({"MANAGER"})
    @GetMapping
    public ResponseEntity<List<ParameterDTO.response>> getAll() {
        return ResponseEntity.ok().body(parameterService.getAllParameters());
    }

    // READ ONE 
    @GetMapping("/{id}")
    public ResponseEntity<ParameterDTO.response> getById(@PathVariable UUID id) {
        ParameterDTO.response response = parameterService.findById(id);
        return ResponseEntity.ok(response);
    }

    // UPDATE 
    @PutMapping("/{id}")
    public ResponseEntity<ParameterDTO.response> update(
            @PathVariable UUID id, 
            @RequestBody ParameterDTO.request request) {
        ParameterDTO.response response = parameterService.update(id, request);
        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        parameterService.delete(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }

    // ACTUALIZAR CACHÉ DE PARAMETROS
    @GetMapping("/update")
    public ResponseEntity<String> clearParametersCache() {
        cacheManager.getCache("parameters").clear();
        log.info("!!! CACHÉ 'parameters' ACTUALIZADA CORRECTAMENTE !!!");
        return ResponseEntity.ok("La caché de parámetros ha sido actualizada correctamente.");
    }
}