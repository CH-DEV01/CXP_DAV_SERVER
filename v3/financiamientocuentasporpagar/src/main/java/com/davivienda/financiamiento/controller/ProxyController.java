package com.davivienda.financiamiento.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
public class ProxyController {

    private static final Logger log = LoggerFactory.getLogger(ProxyController.class);

    @Value("${proxy.target.base}")
    private String targetBase;

    // Headers que NO se deben reenviar al servidor destino
    private static final Set<String> EXCLUDED_REQUEST_HEADERS = Set.of(
        "host", "connection", "transfer-encoding", "upgrade"
    );

    // Headers que NO se deben retornar al cliente
    private static final Set<String> EXCLUDED_RESPONSE_HEADERS = Set.of(
        "transfer-encoding", "connection"
    );

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Reverse proxy genérico: reenvía cualquier petición a /api/** y rutas de autenticación
     * hacia APIFinanciamientoEmpresas en el servidor interno,
     * adjuntando todos los headers originales incluyendo la cookie "token".
     */
    @RequestMapping({"/api/**", "/validate-session", "/login", "/logout"})
    public ResponseEntity<Object> proxy(HttpServletRequest request) throws IOException {

        // Construir la ruta destino: extraer lo que viene después del context path
        String requestUri = request.getRequestURI();
        // requestUri llega como /financiamientocuentasporpagar/api/... o /financiamientocuentasporpagar/validate-session
        // Se extrae a partir de /api/ o de la ruta raíz del contexto
        int apiIndex = requestUri.indexOf("/api/");
        String subPath;
        if (apiIndex >= 0) {
            subPath = requestUri.substring(apiIndex);
        } else {
            // Rutas que no son /api/** (ej: /validate-session, /login)
            String contextPath = request.getContextPath(); // /financiamientocuentasporpagar
            subPath = contextPath.isEmpty() ? requestUri : requestUri.substring(contextPath.length());
        }

        // Preservar query string si existe
        String queryString = request.getQueryString();
        String targetUrl = targetBase + subPath + (queryString != null ? "?" + queryString : "");

        log.info("[ReverseProxy] {} {} -> {}", request.getMethod(), requestUri, targetUrl);

        // ── Construir headers ────────────────────────────────────────────────
        HttpHeaders headers = new HttpHeaders();

        // Copiar headers del request original (excepto los excluidos y Cookie, que se controla abajo)
        Collections.list(request.getHeaderNames()).forEach(headerName -> {
            String lower = headerName.toLowerCase();
            if (!EXCLUDED_REQUEST_HEADERS.contains(lower) && !"cookie".equals(lower)) {
                List<String> values = Collections.list(request.getHeaders(headerName));
                headers.addAll(headerName, values);
            }
        });

        // Reenviar SOLO la cookie "token" al API interno — nunca otras cookies del browser
        String tokenValue = extractToken(request);
        if (tokenValue != null) {
            headers.set("Cookie", "token=" + tokenValue);
        }

        // ── Construir body ───────────────────────────────────────────────────
        byte[] body = StreamUtils.copyToByteArray(request.getInputStream());
        HttpEntity<byte[]> entity = new HttpEntity<>(body.length > 0 ? body : null, headers);

        // ── Ejecutar la llamada al servidor destino ──────────────────────────
        try {
            HttpMethod method = HttpMethod.valueOf(request.getMethod());

            ResponseEntity<byte[]> response = restTemplate.exchange(
                targetUrl,
                method,
                entity,
                byte[].class
            );

            // Copiar headers de respuesta (excepto los excluidos)
            HttpHeaders responseHeaders = new HttpHeaders();
            response.getHeaders().forEach((name, values) -> {
                if (!EXCLUDED_RESPONSE_HEADERS.contains(name.toLowerCase())) {
                    responseHeaders.addAll(name, values);
                }
            });

            log.info("[ReverseProxy] Respuesta: {} para {}", response.getStatusCode(), subPath);
            return ResponseEntity
                .status(response.getStatusCode())
                .headers(responseHeaders)
                .body(response.getBody());

        } catch (HttpClientErrorException e) {
            log.error("[ReverseProxy] Error HTTP {} en {}: {}", e.getStatusCode(), subPath, e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (Exception e) {
            log.error("[ReverseProxy] Error inesperado en {}: {} - {}", subPath, e.getClass().getSimpleName(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ── utilidades ───────────────────────────────────────────────────────────

    private String extractToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
