package com.davivienda.factoraje.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.davivienda.factoraje.service.OperationsService;
import com.davivienda.factoraje.service.ParameterService;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(OperationsService.class);

    //private static final String SECRET_STRING = "estaEsUnaClaveSecretaMuchoMasLargaYSeguraParaHS256";

    private static final String JWT_SECRET_KEY_PARAM = "jwt.secret.key";
    private static final String EXPIRATION_TIME_KEY_PARAM = "jwt.time.expiration";

    //private static final long EXPIRATION_TIME = 5 * 60 * 1000; // 5 minutos

    private final Key secretKey;
    private final long expirationTimeMs;
    private final ParameterService parameterService;

    public JwtUtil(ParameterService parameterService) {
        this.parameterService = parameterService;
        
        String secretString = getParameter(JWT_SECRET_KEY_PARAM)
                .orElseThrow(() -> new IllegalStateException(
                    "El secreto JWT no se encuentra configurado en los parámetros: " + JWT_SECRET_KEY_PARAM
                ));
        
        this.secretKey = Keys.hmacShaKeyFor(secretString.getBytes());
        log.info("Clave secreta para JWT cargada correctamente.");

        String expirationStr = getParameter(EXPIRATION_TIME_KEY_PARAM)
            .orElseThrow(() -> new IllegalStateException(
                "El tiempo de expiración de JWT no se encuentra configurado: " + EXPIRATION_TIME_KEY_PARAM
            ));
        
        try {
            // Se convierte el string a long y se asigna al campo de la instancia
            this.expirationTimeMs = Long.parseLong(expirationStr);
            log.info("Tiempo de expiración para JWT cargado: {} ms.", this.expirationTimeMs);
        } catch (NumberFormatException e) {
            log.error("El valor para el tiempo de expiración no es un número válido: '{}'", expirationStr);
            throw new IllegalStateException("Valor de tiempo de expiración de JWT no es válido: " + expirationStr, e);
        }
    }

    private Optional<String> getParameter(String key) {
        try {
            String value = parameterService.getValueByKey(key);
            return Optional.ofNullable(value); 
        } catch (RuntimeException e) {
            log.warn("No se pudo obtener el parámetro '{}'.", key, e); 
            return Optional.empty();
        }
    }

    public String generateToken(String DUI) {
        
        return Jwts.builder()
                .setSubject(DUI)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + this.expirationTimeMs))
                .signWith(this.secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(this.secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("El token JWT ha expirado: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("El token JWT es inválido (mal formado o firma incorrecta): {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al validar el token JWT.", e);
        }
        return false;
    }

    public String getDUIFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
