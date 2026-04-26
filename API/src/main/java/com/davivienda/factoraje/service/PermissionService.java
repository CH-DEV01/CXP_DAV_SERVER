package com.davivienda.factoraje.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.davivienda.factoraje.domain.dto.Permissions.PermissionRequestDTO;
import com.davivienda.factoraje.domain.dto.Permissions.PermissionResponseDTO;
import com.davivienda.factoraje.domain.model.PermissionModel;
import com.davivienda.factoraje.exception.ResourceNotFoundException;
import com.davivienda.factoraje.repository.PermissionRepository;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private static final Logger log = LoggerFactory.getLogger(PermissionService.class);

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
        log.info("PermissionService initialized");
    }

    public PermissionResponseDTO create(PermissionRequestDTO dto) {

        if (dto == null || dto.getPermissionName() == null
                || dto.getPermissionName().trim().isEmpty()) {
            log.warn("create called with invalid dto");
            throw new IllegalArgumentException("'permissionName' es obligatorio");
        }

        PermissionModel model = new PermissionModel();
        model.setPermissionName(dto.getPermissionName());
        model.setPermissionDescription(dto.getPermissionDescription());

        log.debug("Saving permission {}", dto.getPermissionName());
        PermissionModel saved = permissionRepository.save(model);

        PermissionResponseDTO response = new PermissionResponseDTO();
        response.setPermission_id(saved.getPermissionId());
        response.setPermissionName(saved.getPermissionName());
        response.setPermissionDescription(saved.getPermissionDescription());
        return response;
    }

    public List<PermissionModel> getAll() {
        log.debug("Fetching all permissions");
        return permissionRepository.findAll();
    }

    public PermissionModel getById(UUID id) {
        if (id == null) {
            log.warn("getById called with null id");
            throw new IllegalArgumentException("El parámetro 'id' no puede ser nulo");
        }
        return permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con id=" + id));
    }
}
