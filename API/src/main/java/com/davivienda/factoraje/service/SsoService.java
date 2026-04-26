package com.davivienda.factoraje.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.davivienda.factoraje.controller.AgreementController;
import com.davivienda.factoraje.domain.dto.otc.OtcDTORequest;
import com.davivienda.factoraje.domain.dto.otc.OtcDTOResponse;
import com.davivienda.factoraje.domain.dto.otc.UserData;
import com.davivienda.factoraje.domain.dto.sso.DataSession;
import com.davivienda.factoraje.domain.dto.sso.HandoffDTOResponse;
import com.davivienda.factoraje.domain.model.PendingSessionModel;
import com.davivienda.factoraje.domain.model.UserModel;
import com.davivienda.factoraje.exception.BusinessException;
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
    private static final Logger log = LoggerFactory.getLogger(AgreementController.class);

    public SsoService(RestTemplate restTemplate, ParameterService parameterService, UserService userService, JwtUtil jwtUtil, PendingSessionRepository pendingSessionRepository) {
        this.restTemplate = restTemplate;
        this.parameterService = parameterService;
        this.pendingSessionRepository = pendingSessionRepository;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        log.info("Sso Service initialized");
    }

    // Caso de prueba 1: Se reciben appCode, bearerToken, otc, se hacen las validaciones respectivas y se retorna el sessionId

    public HandoffDTOResponse test1(String appCode, String otc, String bearerTokenRequest) {
        
        String myBearerToken = parameterService.getValueByKey("sso.fxp.bearer.token"); 
        String myAppCode = parameterService.getValueByKey("sso.app.code");

        if (bearerTokenRequest == null || !bearerTokenRequest.equals("Bearer " + myBearerToken)) {
            throw new RuntimeException("Bearer Token inválido: No autorizado");
        }

        if (myAppCode != null && !myAppCode.equals(appCode)) {
            throw new RuntimeException("Código de aplicación incorrecto");
        }

        OtcDTOResponse otcResponse = new OtcDTOResponse();
    
        otcResponse.setSuccess(true);

        UserData userData = new UserData();

        userData.setApp(appCode);
        userData.setOtc(otc);
        userData.setUserName("013316371");
        userData.setUserEmail("jose.pineda@davivienda.com.sv");
        userData.setStatus("validated");
        userData.setTimestamp("");

        otcResponse.setData(userData);
        otcResponse.setMessage("Validación exitosa");

        try {
            if (otcResponse.getSuccess() && userData.getStatus().equals("validated")) {
                
                String sessionToken = UUID.randomUUID().toString();

                PendingSessionModel pendingSession = new PendingSessionModel();
                pendingSession.setSessionToken(sessionToken);
                pendingSession.setUserDui(userData.getUserName());
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
                throw new RuntimeException("OTC no validado por Davivienda (Simulado)");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error en proceso de Handoff: " + e.getMessage());
        }    
    }

    // Caso de prueba 2: Petición para validar OTC

    public String test2(String otc) {
             
        String payBearerToken = parameterService.getValueByKey("sso.pay.bearer.token");
        String payDaviviendaUrl = parameterService.getValueByKey("sso.pay.davivienda.url");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + payBearerToken); 
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        OtcDTORequest body = new OtcDTORequest();
        body.setOtc(otc);

        HttpEntity<OtcDTORequest> requestEntity = new HttpEntity<>(body, headers);

        try {
            
            ResponseEntity<OtcDTOResponse> response = restTemplate.exchange(
                    payDaviviendaUrl.concat("app/api.nsf/api-validate-otc"),
                    HttpMethod.POST,
                    requestEntity,
                    OtcDTOResponse.class
            );

            if (response.getBody() != null && response.getBody().getSuccess()) {
                
                String status = response.getBody().getData().getStatus();
                if (!"validated".equals(status)) {
                    log.info("OTC no validado por Davivienda");
                    throw new RuntimeException("OTC no validado por Davivienda");
                }

                return "OTC validado exitosamente";

            } else {
                log.info("Respuesta fallida desde el servidor externo");
                throw new RuntimeException("Respuesta fallida desde el servidor externo");
            }

        } catch (HttpStatusCodeException e) {
           
            String errorBody = e.getResponseBodyAsString();
            log.error("Error del servidor externo: {}", errorBody);
            
            throw new RuntimeException("Error Davivienda [" + e.getStatusCode() + "]: " + errorBody);

        } catch (Exception e) {
            log.error("Error general: {}", e.getMessage());
            throw new RuntimeException("Error consumiendo API: " + e.getMessage());
        }    
    }

    // Caso de prueba 3: SSO completo
    public HandoffDTOResponse processHandoff(String appCode, String otc, String bearerTokenRequest) {
        log.info("[Handoff-Start] Iniciando proceso para AppCode: {} con OTC: {}", appCode, otc);

        try {
            // 1. CARGA DE PARÁMETROS (Configuración)
            String myBearerToken = parameterService.getValueByKey("sso.fxp.bearer.token");
            String payBearerToken = parameterService.getValueByKey("sso.pay.bearer.token");
            String myAppCode = parameterService.getValueByKey("sso.app.code");
            String payDaviviendaUrl = parameterService.getValueByKey("sso.pay.davivienda.url");

            // 2. VALIDACIONES DE SEGURIDAD 
            if (bearerTokenRequest == null || !bearerTokenRequest.equals("Bearer " + myBearerToken)) {
                log.warn("[Handoff-Business] Intento de acceso no autorizado. Token inválido.");
                throw new BusinessException("No autorizado: Bearer Token inválido");
            }

            if (myAppCode == null || !myAppCode.equals(appCode)) {
                log.warn("[Handoff-Business] AppCode no coincide. Recibido: {}, Esperado: {}", appCode, myAppCode);
                throw new BusinessException("Código de aplicación incorrecto");
            }

            // 3. PREPARACIÓN DE PETICIÓN EXTERNA
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + payBearerToken);
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

            OtcDTORequest body = new OtcDTORequest();
            body.setOtc(otc);
            HttpEntity<OtcDTORequest> requestEntity = new HttpEntity<>(body, headers);

            log.info("[Handoff-External] Consultando validación en Davivienda...");

            // 4. LLAMADA AL SERVICIO EXTERNO
            ResponseEntity<OtcDTOResponse> response = restTemplate.exchange(
                    payDaviviendaUrl.concat("app/api.nsf/api-validate-otc"),
                    HttpMethod.POST,
                    requestEntity,
                    OtcDTOResponse.class
            );

            // --- NUEVO LOG DE INSPECCIÓN ---
            if (response.getBody() != null) {
                log.info("[Handoff-External-Response] Cuerpo recibido: {}", response.getBody().toString());
            } else {
                log.warn("[Handoff-External-Response] El cuerpo de la respuesta está VACÍO (null)");
            }
            // -------------------------------

            OtcDTOResponse otcResponse = response.getBody();

            // 5. VALIDACIÓN DE RESPUESTA DE NEGOCIO (Proveedor Externo)
            if (otcResponse == null || !Boolean.TRUE.equals(otcResponse.getSuccess())) {
                log.warn("[Handoff-Business] El proveedor rechazó la solicitud o envió respuesta vacía.");
                throw new BusinessException("El OTC no pudo ser procesado por el proveedor");
            }

            String status = otcResponse.getData().getStatus();
            if (!"validated".equals(status)) {
                log.warn("[Handoff-Business] OTC no validado. Status devuelto: {}", status);
                throw new BusinessException("OTC no tiene estado 'validated'");
            }

            // 6. PROCESAMIENTO EXITOSO Y PERSISTENCIA
            String dui = otcResponse.getData().getUser();
            String sessionToken = UUID.randomUUID().toString();

            PendingSessionModel pendingSession = new PendingSessionModel();
            pendingSession.setSessionToken(sessionToken);
            pendingSession.setUserDui(dui);
            pendingSession.setCreatedAt(LocalDateTime.now());
            pendingSession.setExpiresAt(LocalDateTime.now().plusMinutes(5));
            
            pendingSessionRepository.save(pendingSession);
            log.info("[Handoff-Success] Sesión creada para DUI: {}. Token generado: {}", dui, sessionToken);

            // 7. CONSTRUCCIÓN DE RESPUESTA
            HandoffDTOResponse handoffResponse = new HandoffDTOResponse();
            handoffResponse.setSuccess(true);
            DataSession session = new DataSession();
            session.setStatus("ok");
            session.setToken(sessionToken);
            session.setTimestamp(OffsetDateTime.now());
            handoffResponse.setData(session);

            return handoffResponse;

        } catch (BusinessException e) {
            // AQUÍ CAEN: Tokens inválidos, AppCode erróneo, OTC no validado por Davivienda.
            log.warn("[Handoff-Logic-Error] Error de regla de negocio: {}", e.getMessage());
            throw e; 

        } catch (HttpStatusCodeException e) {
            // AQUÍ CAEN: Errores HTTP (404, 500, 401 del servidor de Davivienda).
            log.error("[Handoff-External-Error] Error de comunicación (HTTP {}). Response: {}", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error en comunicación con proveedor externo");

        } catch (Exception e) {
            // AQUÍ CAEN: NullPointer, errores de base de datos, errores inesperados.
            log.error("[Handoff-Critical-Error] Error no controlado en el proceso", e);
            throw new RuntimeException("Error interno del sistema: " + e.getMessage());
        }
    }

    // PETICION REAL

    // public HandoffDTOResponse processHandoff(String appCode, String otc, String bearerTokenRequest) {

    //     log.info(appCode);
    //     log.info(bearerTokenRequest);
    //     log.info(otc);
            
    //     String myBearerToken = parameterService.getValueByKey("sso.fxp.bearer.token"); 
    //     String payBearerToken = parameterService.getValueByKey("sso.pay.bearer.token");
    //     String myAppCode = parameterService.getValueByKey("sso.app.code");
    //     String payDaviviendaUrl = parameterService.getValueByKey("sso.pay.davivienda.url").concat("/api.nsf/api-validate-otc");

    //     if (bearerTokenRequest == null || !bearerTokenRequest.equals("Bearer " + myBearerToken)) {
    //         log.info("Bearer Token inválido");
    //         throw new RuntimeException("Bearer Token inválido: No autorizado");
    //     }

    //     if (myAppCode == null || !myAppCode.equals(appCode)) {
    //         log.info("Código de aplicación incorrecto");
    //         throw new RuntimeException("Código de aplicación incorrecto");
    //     }

    //     HttpHeaders headers = new HttpHeaders();
    //     headers.setContentType(MediaType.APPLICATION_JSON);
    //     headers.set("Authorization", "Bearer " + payBearerToken); 
    //     headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

    //     OtcDTORequest body = new OtcDTORequest();
    //     OtcDTOResponse OtcResponse = new OtcDTOResponse();
    //     body.setOtc(otc);

    //     HttpEntity<OtcDTORequest> requestEntity = new HttpEntity<>(body, headers);

    //     try {
            
    //         ResponseEntity<OtcDTOResponse> response = restTemplate.exchange(
    //                 payDaviviendaUrl,
    //                 HttpMethod.POST,
    //                 requestEntity,
    //                 OtcDTOResponse.class
    //         );

    //         if (response.getBody() != null && response.getBody().getSuccess()) {
                
    //             String status = response.getBody().getUserData().getStatus();
    //             if (!"validated".equals(status)) {
    //                 log.info("OTC no validado por Davivienda");
    //                 throw new RuntimeException("OTC no validado por Davivienda");
    //             }

    //             String dui = OtcResponse.getUserData().getUserName();
    //             String sessionToken = UUID.randomUUID().toString();

    //             PendingSessionModel pendingSession = new PendingSessionModel();
    //             pendingSession.setSessionToken(sessionToken);
    //             pendingSession.setUserDui(dui);
    //             pendingSession.setCreatedAt(LocalDateTime.now());
    //             pendingSession.setExpiresAt(LocalDateTime.now().plusMinutes(5));
                
    //             pendingSessionRepository.save(pendingSession);

    //             HandoffDTOResponse handoffResponse = new HandoffDTOResponse();
    //             handoffResponse.setSuccess(true);

    //             DataSession session = new DataSession();

    //             session.setStatus("ok");
    //             session.setToken(sessionToken);
    //             session.setTimestamp(OffsetDateTime.now());

    //             handoffResponse.setData(session);
                
    //             return handoffResponse;

    //         } else {
    //             log.info("Respuesta fallida desde el servidor externo");
    //             throw new RuntimeException("Respuesta fallida desde el servidor externo");
    //         }

    //     } catch (Exception e) {
    //         log.info("Error consumiendo API Validate-OTC");
    //         throw new RuntimeException("Error consumiendo API Validate-OTC: " + e.getMessage());
    //     }    
    // }

    // PETICION SIMULADA

    // public HandoffDTOResponse processHandoffLogin(String appCode, String otc, String bearerTokenRequest) {
        
    //     String myBearerToken = parameterService.getValueByKey("sso.fxp.bearer.token"); 
    //     String myAppCode = parameterService.getValueByKey("sso.app.code");

    //     if (bearerTokenRequest == null || !bearerTokenRequest.equals("Bearer " + myBearerToken)) {
    //         throw new RuntimeException("Bearer Token inválido: No autorizado");
    //     }

    //     if (myAppCode != null && !myAppCode.equals(appCode)) {
    //         throw new RuntimeException("Código de aplicación incorrecto");
    //     }

    //     OtcDTOResponse otcResponse = new OtcDTOResponse();
    //     UserData userData = new UserData();

    //     otcResponse.setMessage("Validación exitosa");
    //     otcResponse.setSuccess(true);

    //     userData.setApp(appCode);
    //     userData.setOtc(otc);
    //     userData.setStatus("validated");
    //     userData.setUserEmail("jose.pineda@davivienda.com.sv");
    //     userData.setUserName("013316371");
    //     userData.setTimestamp(OffsetDateTime.now());

    //     otcResponse.setUserData(userData);

    //     try {
    //         if (otcResponse.getSuccess() && userData.getStatus().equals("validated")) {
                
    //             String sessionToken = UUID.randomUUID().toString();

    //             PendingSessionModel pendingSession = new PendingSessionModel();
    //             pendingSession.setSessionToken(sessionToken);
    //             pendingSession.setUserDui(userData.getUserName());
    //             pendingSession.setCreatedAt(LocalDateTime.now());
    //             pendingSession.setExpiresAt(LocalDateTime.now().plusMinutes(5));
                
    //             pendingSessionRepository.save(pendingSession);

    //             HandoffDTOResponse handoffResponse = new HandoffDTOResponse();
    //             handoffResponse.setSuccess(true);

    //             DataSession session = new DataSession();
    //             session.setStatus("ok");
    //             session.setToken(sessionToken);
    //             session.setTimestamp(OffsetDateTime.now());

    //             handoffResponse.setData(session);
                
    //             return handoffResponse;

    //         } else {
    //             throw new RuntimeException("OTC no validado por Davivienda (Simulado)");
    //         }

    //     } catch (Exception e) {
    //         throw new RuntimeException("Error en proceso de Handoff: " + e.getMessage());
    //     }    
    // }

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