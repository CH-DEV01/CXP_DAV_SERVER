package com.davivienda.financiamiento.controller;

import com.davivienda.financiamiento.domain.dto.StartSessionRequestDTO;
import com.davivienda.financiamiento.domain.dto.StartSessionResponseDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
public class SsoController {

    private static final Logger log = LoggerFactory.getLogger(SsoController.class);

    @Autowired
    private RestTemplate restTemplate;

    // Token esperado — el que envía Pay Davivienda en el form POST (paso 7).
    // En producción este valor se busca en base de datos / cache, no hardcodeado.

    /**
     * Paso 8 — Endpoint de recepción SSO.
     *
     * Recibe el sessionToken de Pay vía POST application/x-www-form-urlencoded,
     * lo compara con el token esperado, invalida la sesión anterior para prevenir
     * session fixation (OWASP A07), genera un nuevo session token propio y
     * redirige al index.
     *
     * URL: POST /start-session?OpenAgent
     */
    @PostMapping("/start-session")
    public RedirectView startSession(
            @RequestParam("sessionToken") String sessionToken,
            HttpServletRequest request,
            HttpServletResponse httpResponse) {

        log.info("[start-session] Solicitud recibida - IP: {}, sessionToken recibido: {}",
                request.getRemoteAddr(), sessionToken != null ? "presente" : "ausente");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        StartSessionRequestDTO body = new StartSessionRequestDTO();
        body.setSessionToken(sessionToken);

        HttpEntity<StartSessionRequestDTO> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<StartSessionResponseDTO> response = restTemplate.exchange(
            //"http://localhost:8080/api/sso/activateSession",
            "https://sv4148lap.daviviendasv.com:8443/APIFinanciamientoEmpresas/api/sso/activateSession",
            HttpMethod.POST,
            requestEntity,
            StartSessionResponseDTO.class
        );

        if (response.getBody() != null && response.getBody().getSuccess()) {
            String jwt = response.getBody().getJwt();

            // Escribir el JWT directamente como cookie "token" — evita la cookie de sesión duplicada
            Cookie tokenCookie = new Cookie("token", jwt);
            tokenCookie.setHttpOnly(true);
            tokenCookie.setPath("/");
            tokenCookie.setMaxAge(1800); // 30 minutos
            httpResponse.addCookie(tokenCookie);
        }
         //return new RedirectView("https://devpay.davivienda.com.sv/financiamientocuentasporpagar/");
        return new RedirectView("/financiamientocuentasporpagar/");
    }

    /**
     * Endpoint que consulta index.html vía fetch para obtener los datos de sesión.
     * Responde solo si la sesión es válida y autenticada.
     */
    @GetMapping("/api/session-info")
    public ResponseEntity<Map<String, Object>> sessionInfo(HttpServletRequest request) {

        String jwt = null;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("token".equals(c.getName())) {
                    jwt = c.getValue();
                    break;
                }
            }
        }

        if (jwt == null || jwt.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("autenticado", false));
        }

        return ResponseEntity.ok(Map.of(
                "autenticado", true,
                "nuevoSessionToken", jwt
        ));
    }

}
