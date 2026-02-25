package com.davivienda.factoraje.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import com.davivienda.factoraje.utils.JwtUtil;
import com.davivienda.factoraje.domain.dto.Auth.RegisterDTORequest;
import com.davivienda.factoraje.domain.dto.Auth.RegisterDTOResponse;
import com.davivienda.factoraje.domain.dto.Users.GetAllUsersResponseDTO;
import com.davivienda.factoraje.domain.model.EntityModel;
import com.davivienda.factoraje.domain.model.RoleModel;
import com.davivienda.factoraje.domain.model.UserModel;
import com.davivienda.factoraje.exception.ResourceNotFoundException;

import java.util.Collections;

@Service
public class AuthService {

    private final UserService userService;
    private final EntityService entityService;
    private final RoleService roleService;
    private final JwtUtil jwtUtil;

    public AuthService(JwtUtil jwtUtil, UserService userService, EntityService entityService, RoleService roleService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.entityService = entityService;
        this.roleService = roleService;
    }

    public RegisterDTOResponse linkUser(RegisterDTORequest registerRequest) {

        RegisterDTOResponse response = new RegisterDTOResponse();

        // Arreglar esta logica para el registro del usuario

        // UserModel existingUser = userService.findByEmail(registerRequest.getEmail());


        // if (existingUser != null) {
        //     throw new ResourceAlreadyExistsException("Un usuario con este email ya existe.");
        // }

        // EntityModel entity = entityService.getEntityById(registerRequest.getEntityId());
        // if (entity == null) {
        //     throw new RuntimeException("Entity not found");
        // }

        if (registerRequest.getEntityId() == null) {
            throw new IllegalArgumentException("El ID de la entidad no puede ser nulo.");
        }
        EntityModel entity = entityService.getEntityById(registerRequest.getEntityId());
        if (entity == null) {
            throw new ResourceNotFoundException("La entidad con id " + registerRequest.getEntityId() + " no fue encontrada.");
        }

        // RoleModel role = roleService.getById(registerRequest.getRoleId());

        // if (role == null) {
        //     throw new RuntimeException("PAYER role not found");
        // }

        if (registerRequest.getRoleId() == null) {
            throw new IllegalArgumentException("El ID del rol no puede ser nulo.");
        }
        RoleModel role = roleService.getById(registerRequest.getRoleId());
        if (role == null) {
            throw new ResourceNotFoundException("El rol con id " + registerRequest.getRoleId() + " no fue encontrado.");
        }

        UserModel user = new UserModel();
        user.setEmail(registerRequest.getEmail());
        user.setName(registerRequest.getName());
        user.setDui(registerRequest.getDui());
        user.setEntity(entity);
        user.setRole(role);

        userService.save(user);

        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setEntityId(user.getEntity().getId());
        response.setEntityName(user.getEntity().getName());
        response.setDui(user.getDui());
        return response;
    }

    public List<GetAllUsersResponseDTO> getAllUsers() {

        //List<UserModel> users = userService.getAll();

        List<UserModel> users = userService.getAll();
        if (users == null) {
            return Collections.emptyList();
        }

        return users.stream()
                .map(user -> {
                    GetAllUsersResponseDTO dto = new GetAllUsersResponseDTO();
                    dto.setId(user.getId());
                    dto.setDui(user.getDui());
                    dto.setName(user.getName());
                    dto.setRoleName(user.getRole() != null ? user.getRole().getRoleName() : "N/A");
                    dto.setEntityName(user.getEntity() != null ? user.getEntity().getName() : "N/A");
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void login(String DUI, HttpServletResponse response) {
        
        String jwt = jwtUtil.generateToken(DUI);

        Cookie cookie = new Cookie("token", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Solo en producción con HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(300000);

        response.addCookie(cookie);
    }

    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0); 
        response.addCookie(cookie);
    }
}
