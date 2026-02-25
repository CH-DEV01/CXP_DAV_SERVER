package com.davivienda.factoraje.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.davivienda.factoraje.domain.dto.Roles.RoleRequestDTO;
import com.davivienda.factoraje.domain.dto.Roles.RoleResponseDTO;
import com.davivienda.factoraje.domain.model.PermissionModel;
import com.davivienda.factoraje.domain.model.RoleModel;
import com.davivienda.factoraje.exception.ResourceNotFoundException;
import com.davivienda.factoraje.repository.PermissionRepository;
import com.davivienda.factoraje.repository.RoleRepository;

@Service
public class RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleService.class);
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        log.info("RoleService initialized");
    }

    public RoleResponseDTO create(RoleRequestDTO dto) {
        if (dto == null || dto.getRoleName() == null || dto.getRoleName().trim().isEmpty()) {
            log.warn("create called with invalid dto");
            throw new IllegalArgumentException("'roleName' es obligatorio");
        }
        RoleModel model = new RoleModel();
        model.setRoleName(dto.getRoleName());
        model.setRoleDescription(dto.getRoleDescription());

        log.debug("Saving role {}", dto.getRoleName());
        RoleModel saved = roleRepository.save(model);

        RoleResponseDTO resp = new RoleResponseDTO();
        resp.setRole_id(saved.getRoleId());
        resp.setRoleName(saved.getRoleName());
        resp.setRoleDescription(saved.getRoleDescription());
        return resp;
    }

    public RoleModel getById(UUID id) {
        if (id == null) {
            log.warn("getById called with null id");
            throw new IllegalArgumentException("roleId es obligatorio");
        }
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("rol no encontrado"));
    }

    public RoleModel getByRoleName(String name) {
        if (name == null || name.trim().isEmpty()) {
            log.warn("getByRoleName called with empty name");
            throw new IllegalArgumentException("El parametro rolename no puede ser null");
        }
        return roleRepository.findByRoleName(name)
                .orElseThrow(() -> new ResourceNotFoundException("El parametro rolename no puede ser null"));
    }

    public Set<RoleResponseDTO> getAll() {
        log.debug("Fetching all roles");
        List<RoleModel> roles = roleRepository.findAll();
        return roles.stream().map(r -> {
            RoleResponseDTO dto = new RoleResponseDTO();
            dto.setRole_id(r.getRoleId());
            dto.setRoleName(r.getRoleName());
            dto.setRoleDescription(r.getRoleDescription());
            dto.setPermissions(r.getPermissions());
            return dto;
        }).collect(Collectors.toSet());
    }

    public RoleResponseDTO assignPermissions(RoleModel role, Set<UUID> permissionIds) {
        if (role == null) {
            throw new IllegalArgumentException("'role' es obligatorio");
        }
        if (permissionIds == null || permissionIds.isEmpty()) {
            throw new IllegalArgumentException("'roleName' es obligatorio");
        }

        log.debug("Assigning {} permissions to role {}", permissionIds.size(), role.getRoleId());
        for (UUID pid : permissionIds) {
            PermissionModel perm = permissionRepository.findById(pid)
                    .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado"));
            role.getPermissions().add(perm);
        }
        RoleModel saved = roleRepository.save(role);

        RoleResponseDTO dto = new RoleResponseDTO();
        dto.setRole_id(saved.getRoleId());
        dto.setRoleName(saved.getRoleName());
        dto.setRoleDescription(saved.getRoleDescription());
        dto.setPermissions(saved.getPermissions());
        return dto;
    }

}
