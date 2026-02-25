package com.davivienda.factoraje.controller;

import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.davivienda.factoraje.auth.PermissionsAllowed;
import com.davivienda.factoraje.auth.RolesAllowed;
import com.davivienda.factoraje.domain.dto.Auth.RegisterDTORequest;
import com.davivienda.factoraje.domain.dto.Auth.RegisterDTOResponse;
import com.davivienda.factoraje.domain.dto.Users.GetAllUsersResponseDTO;
import com.davivienda.factoraje.domain.dto.Users.UserLoggedResponseDTO;
import com.davivienda.factoraje.domain.model.UserModel;
import com.davivienda.factoraje.exception.ResourceNotFoundException;
import com.davivienda.factoraje.service.AuthService;
import com.davivienda.factoraje.service.UserService;
import com.davivienda.factoraje.utils.JwtUtil;
import javax.servlet.http.Cookie;
import java.util.Arrays;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, AuthService authService, JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.authService = authService;
        log.info("UserController initialized");
    }

    @RolesAllowed({"MANAGER"})
    @PermissionsAllowed({"link_user_execute"})
    @PostMapping("/linkUser")
    public ResponseEntity<?> linkUser(@RequestBody RegisterDTORequest linkRequest) {
        log.info("POST /api/users/linkUser - link user request: {}", linkRequest);
        if (linkRequest == null) {
            log.warn("RegisterDTORequest is null");
            return ResponseEntity.badRequest().body("El cuerpo de la petición no puede ser vacío");
        }

        RegisterDTOResponse response = authService.linkUser(linkRequest);
        log.info("User linked successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserData(HttpServletRequest request) {
        log.info("GET /api/users/user - fetch logged user data");
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                log.warn("No cookies found in request");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No token cookie present");
            }
            Optional<String> tokenOpt = Arrays.stream(cookies)
                    .filter(c -> "token".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst();
            System.out.println(tokenOpt.get());
            if (!tokenOpt.isPresent() || !jwtUtil.validateToken(tokenOpt.get())) {
                log.warn("Invalid or missing JWT token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
            }
            String token = tokenOpt.get();
            String DUI = jwtUtil.getDUIFromToken(token);
            System.out.println(DUI);
            log.debug("DUI extracted from token: {}", DUI);

            UserModel user = userService.findUserByDUI(DUI);
                    
            UserLoggedResponseDTO userResponse = new UserLoggedResponseDTO();
            userResponse.setId(user.getId());
            userResponse.setName(user.getName());
            userResponse.setEmail(user.getEmail());
            userResponse.setDui(user.getDui());
            userResponse.setEntityId(user.getEntity().getId());
            userResponse.setEntityName(user.getEntity().getName());
            userResponse.setEntityType(user.getEntity().getEntityType());
            userResponse.setAuthenticationMode(user.getEntity().getAuthenticationMode());
            userResponse.setRole(user.getRole());

            return ResponseEntity.ok(userResponse);
        } catch (ResourceNotFoundException ex) {
            log.warn(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            log.error("Error fetching user data", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al obtener datos de usuario");
        }
    }

    @RolesAllowed({"MANAGER"})
    @PermissionsAllowed({"link_user_view"})
    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getAllUsers() {
        log.info("GET /api/users/getAllUsers - fetch all users via authService");
        List<GetAllUsersResponseDTO> users = authService.getAllUsers();
        if (users.isEmpty()) {
            log.info("No users found via authService");
            return ResponseEntity.noContent().build();
        }
        log.info("Found {} users via authService", users.size());
        return ResponseEntity.ok(users);
    }
}
