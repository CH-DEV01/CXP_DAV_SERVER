package com.davivienda.factoraje.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.davivienda.factoraje.domain.dto.otc.OtcDTORequest;
import com.davivienda.factoraje.domain.dto.otc.OtcDTOResponse;
import com.davivienda.factoraje.domain.dto.sso.DataSession;
import com.davivienda.factoraje.domain.dto.sso.HandoffDTOResponse;
import com.davivienda.factoraje.domain.model.PendingSessionModel;
import com.davivienda.factoraje.domain.model.UserModel;
import com.davivienda.factoraje.repository.PendingSessionRepository;
import com.davivienda.factoraje.utils.JwtUtil;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;
import javax.transaction.Transactional;

@Service
public class SsoService {

    private final ParameterService parameterService;
    private final RestTemplate restTemplate;
    private final PendingSessionRepository pendingSessionRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public SsoService(RestTemplate restTemplate, ParameterService parameterService, UserService userService, JwtUtil jwtUtil, PendingSessionRepository pendingSessionRepository) {
        this.restTemplate = restTemplate;
        this.parameterService = parameterService;
        this.pendingSessionRepository = pendingSessionRepository;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    public HandoffDTOResponse processHandoff(String appCode, String otc, String authHeader) {
        
        // 1. Obtener parámetros de configuración
        String myBearerToken = parameterService.getValueByKey("sso.fxp.bearer.token"); 
        String payBearerToken = parameterService.getValueByKey("sso.pay.bearer.token");
        String myAppCode = parameterService.getValueByKey("sso.app.code");
        String payDaviviendaUrl = parameterService.getValueByKey("sso.pay.davivienda.url").concat("/api.nsf/api-validate-otc");

        // 2. Validar Bearer Token entrante
        if (authHeader == null || !authHeader.equals("Bearer " + myBearerToken)) {
            throw new RuntimeException("Bearer Token inválido: No autorizado");
        }

        // 3. Validar App Code
        if (myAppCode != null && !myAppCode.equals(appCode)) {
             throw new RuntimeException("Código de aplicación incorrecto");
        }

        // 4. Petición
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + payBearerToken); 
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        OtcDTORequest body = new OtcDTORequest();
        OtcDTOResponse OtcResponse = new OtcDTOResponse();
        body.setOtc(otc);

        HttpEntity<OtcDTORequest> requestEntity = new HttpEntity<>(body, headers);

        try {
            
            ResponseEntity<OtcDTOResponse> response = restTemplate.exchange(
                    payDaviviendaUrl,
                    HttpMethod.POST,
                    requestEntity,
                    OtcDTOResponse.class
            );

            if (response.getBody() != null && response.getBody().getSuccess()) {
                
                String status = response.getBody().getUserData().getStatus();
                if (!"validated".equals(status)) {
                    throw new RuntimeException("OTC no validado por Davivienda");
                }

                String email = OtcResponse.getUserData().getUserName();
                String sessionToken = UUID.randomUUID().toString();

                PendingSessionModel pendingSession = new PendingSessionModel();
                pendingSession.setSessionToken(sessionToken);
                pendingSession.setUserDui(email);
                pendingSession.setCreatedAt(LocalDateTime.now());
                pendingSession.setExpiresAt(LocalDateTime.now().plusMinutes(5));
                
                pendingSessionRepository.save(pendingSession);

                HandoffDTOResponse handoffResponse = new HandoffDTOResponse();
                handoffResponse.setSuccess(true);

                DataSession session = new DataSession();

                session.setStatus("ok");
                session.setToken(sessionToken);
                session.setTimestamp(OffsetDateTime.now());

                handoffResponse.setData(session);
                
                return handoffResponse;

            } else {
                throw new RuntimeException("Respuesta fallida desde el servidor externo");
            }

        } catch (Exception e) {
            // Aquí caerá si Davivienda devuelve un Error 400 (OTC Expirado), 401 (Token Inválido) o si hay Timeout
            throw new RuntimeException("Error consumiendo API Validate-OTC: " + e.getMessage());
        }    
    }

    @Transactional
    public String activateSession(String sessionToken) {

        // 1. Buscar el token en la BD
        PendingSessionModel session = pendingSessionRepository.findById(sessionToken)
            .orElseThrow(() -> new RuntimeException("invalid_session"));

        // 2. Borrarlo inmediatamente para que sea de un solo uso
        pendingSessionRepository.delete(session);

        // 3. Validar si ya había expirado (aunque lo encontremos, verificamos la hora)
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("session_expired");
        }

        // 2. Buscar al usuario en TU base de datos
        UserModel user = userService.findUserByDUI(session.getUserDui());
            
        if (user == null) {
            throw new RuntimeException("user_not_found");
        }

        return jwtUtil.generateToken(user.getDui());
    }
}