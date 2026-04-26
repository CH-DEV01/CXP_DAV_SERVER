package com.davivienda.factoraje.controller;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davivienda.factoraje.service.AuthService;
import com.davivienda.factoraje.utils.JwtUtil;

@RestController
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(JwtUtil jwtUtil, AuthService authService) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        log.info("AuthController initialized");
    }

    @GetMapping("/validate-session")
    public ResponseEntity<Boolean> validateSession(@CookieValue(name = "token", required = false) String token) {
        
        // 1. Verificar si la cookie existe o es valida
        if (token == null || token.isEmpty() || !jwtUtil.validateToken(token)) {
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
        
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        log.info("POST /api/auth/logout - logout attempt");
        try {
            authService.logout(response);
            log.info("Logout successful");
            return ResponseEntity.ok("Sesión cerrada");
        } catch (Exception e) {
            log.error("Error during logout", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al cerrar sesión");
        }
    }

}
