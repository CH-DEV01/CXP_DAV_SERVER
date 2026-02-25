package com.davivienda.factoraje.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.davivienda.factoraje.domain.dto.Users.AssignRolesToUserRequestDTO;
import com.davivienda.factoraje.domain.model.RoleModel;
import com.davivienda.factoraje.domain.model.UserModel;
import com.davivienda.factoraje.exception.ResourceNotFoundException;
import com.davivienda.factoraje.repository.UserRepository;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        log.info("UserService initialized");
    }

    public List<UserModel> getAll() {
        log.debug("Fetching all users");
        return userRepository.findAll();
    }

    public List<UserModel> getPayers() {
        log.debug("Fetching payers");
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() != null && "PAYER".equals(u.getRole().getRoleName()))
                .collect(Collectors.toList());
    }

    public UserModel createPayer(UserModel payer) {
        if (payer == null) {
            throw new IllegalArgumentException("El cuerpo de la petición no puede ser nulo");
        }
        RoleModel payerRole = roleService.getByRoleName("PAYER");
        if (payerRole == null) {
            throw new ResourceNotFoundException("Rol PAYER no existe");
        }
        payer.setRole(payerRole);
        log.debug("Saving new payer {} with role PAYER", payer.getEmail());
        return userRepository.save(payer);
    }

    public UserModel findByEmail(String email) {

        if (email == null) {
            throw new IllegalArgumentException("El email no puede ser nulo o vacío");
        }

        UserModel user = userRepository.findByEmail(email);

        if (user == null) {
            throw new ResourceNotFoundException("Usuario no encontrado con email=" + email);
        }

        return user;
    }

    public UserModel save(UserModel user) {

        if (user == null) {
            throw new IllegalArgumentException("El objeto de usuario no puede ser nulo");
        }

        return userRepository.save(user);
    }

    public UserModel getUserById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("El id de usuario no puede ser nulo");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    public UserModel assignRolesToUser(AssignRolesToUserRequestDTO dto) {
        if (dto == null || dto.getUserId() == null ||
                dto.getRoleIds() == null || dto.getRoleIds().isEmpty()) {
            throw new IllegalArgumentException("UserId u objeto dto no pueden ser null");
        }

        UserModel user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        for (UUID roleId : dto.getRoleIds()) {
            RoleModel role = roleService.getById(roleId);
            user.setRole(role); 
        }
        log.debug("Roles assigned to user {}", user.getId());
        return userRepository.save(user);
    }

    public UserModel findUserByDUI(String dui) {
        if (dui == null) {
            throw new IllegalArgumentException("El dui del usuario no puede ser nulo");
        }
        return userRepository.findByDui(dui)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

}
