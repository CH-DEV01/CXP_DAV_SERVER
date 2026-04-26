package com.davivienda.financiamiento.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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

import com.davivienda.financiamiento.domain.dto.StartSessionRequestDTO;
import com.davivienda.financiamiento.domain.dto.StartSessionResponseDTO;

import java.util.Map;
import java.util.UUID;

@RestController
public class SsoController {

    private final RestTemplate restTemplate;

    // Token esperado — el que envía Pay Davivienda

    public SsoController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final String TOKEN_ESPERADO = "a1b2c3d4-e5f6-7890-abcd-ef1234567890";

    // @PostMapping("/start-session")
    // public RedirectView startSession(
    //         @RequestParam("sessionToken") String sessionToken,
    //         HttpServletRequest request) {

    //     // 1. Comparar el token recibido con el esperado
    //     if (!TOKEN_ESPERADO.equals(sessionToken)) {
    //         return new RedirectView("/index.html?error=invalid_token");
    //     }

    //     // 2. Invalidar sesión anterior — previene session fixation (OWASP A07)
    //     HttpSession sesionAnterior = request.getSession(false);
    //     if (sesionAnterior != null) {
    //         sesionAnterior.invalidate();
    //     }

    //     // 3. Crear nueva sesión limpia
    //     HttpSession nuevaSesion = request.getSession(true);

    //     // 4. Generar nuevo session token propio (distinto al de Pay — ese se descarta)
    //     String nuevoSessionToken = UUID.randomUUID().toString();

    //     // 5. Almacenar en sesión
    //     nuevaSesion.setAttribute("nuevoSessionToken", nuevoSessionToken);
    //     nuevaSesion.setAttribute("autenticado", Boolean.TRUE);
    //     nuevaSesion.setMaxInactiveInterval(1800); // 30 minutos de inactividad

    //     // 6. Redirigir al index — el navegador lleva la cookie JSESSIONID
    //     // automáticamente
    //     return new RedirectView("/index.html");
    // }

    @PostMapping("/start-session")
    public RedirectView startSession(
            @RequestParam("sessionToken") String sessionToken,
            HttpServletRequest request,
            HttpServletResponse responseServlet) {
                
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        StartSessionRequestDTO body = new StartSessionRequestDTO();
        body.setSessionToken(sessionToken);

        HttpEntity<StartSessionRequestDTO> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<StartSessionResponseDTO> response = restTemplate.exchange(
            "http://localhost:8080/api/sso/activateSession",
            HttpMethod.POST,
            requestEntity,
            StartSessionResponseDTO.class
        );

        if (response.getBody() != null && response.getBody().getSuccess()) {
            // Obtener jwt de sesión de la respuesta
            String jwt = response.getBody().getJwt();

            // 2. Crear y configurar la cookie con el JWT
            Cookie jwtCookie = new Cookie("token", jwt);
            jwtCookie.setHttpOnly(false); // Muy importante por seguridad (evita robo por JavaScript/XSS)
            jwtCookie.setPath("/");      // Para que la cookie esté disponible en toda la web
            jwtCookie.setMaxAge(300000); // Tiempo de vida en segundos
            jwtCookie.setSecure(false);   // Asegura que solo se envíe por conexiones HTTPS

            // 3. Añadir la cookie a la respuesta HTTP
            responseServlet.addCookie(jwtCookie);

            // 4. Spring ejecutará la redirección, y la cookie viajará en las cabeceras
            return new RedirectView("http://localhost:5173/financiamientocuentasporpagar/");

        } else {
            return new RedirectView("/index.html?error=invalid_token");
        }
    }

    /**
     * Endpoint que consulta index.html vía fetch para obtener los datos de sesión.
     * Responde solo si la sesión es válida y autenticada.
     */
    @GetMapping("/api/session-info")
    public ResponseEntity<Map<String, Object>> sessionInfo(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null || !Boolean.TRUE.equals(session.getAttribute("autenticado"))) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("autenticado", false));
        }

        return ResponseEntity.ok(Map.of(
                "autenticado", true,
                "nuevoSessionToken", session.getAttribute("nuevoSessionToken")));
    }
}
