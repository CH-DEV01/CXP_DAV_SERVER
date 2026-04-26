package com.davivienda.factoraje.controller;
import com.davivienda.factoraje.service.ParameterService;
import com.davivienda.factoraje.service.SsoService;
import com.davivienda.factoraje.domain.dto.sso.HandoffDTORequest;
import com.davivienda.factoraje.domain.dto.sso.HandoffDTOResponse;
import com.davivienda.factoraje.domain.dto.sso.McsSSODTOResponse;
import com.davivienda.factoraje.domain.dto.sso.SessionTokenDTORequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/sso")
public class SsoController {

    private final SsoService ssoService;
    private final ParameterService parameterService;
    private static final Logger log = LoggerFactory.getLogger(SsoController.class);

    public SsoController(SsoService ssoService, ParameterService parameterService) {
        this.ssoService = ssoService;
        this.parameterService = parameterService;
        log.info("SsoController initialized");
    }

    // Caso de prueba 1: Se reciben appCode, bearerToken, otc, se hacen las validaciones respectivas y se retorna el sessionId
    
    @PostMapping("/test1")
    public ResponseEntity<HandoffDTOResponse> test1(
        @RequestHeader("Authorization") 
        String bearerTokenRequest,                             
        @RequestBody HandoffDTORequest request) {

        log.info("Endpoint /handoff activated");
        log.info("Bearer Token received", bearerTokenRequest);

        HandoffDTOResponse response = ssoService.test1(request.getApp(), request.getOtc(), bearerTokenRequest);
        
        log.info("response to return", response);
        return ResponseEntity.ok(response);

    }

    // Caso de prueba 2: Controlador que ejecuta petición de validación de OTC
    // @PostMapping("/test2")
    // public ResponseEntity<String> test2(                            
    //     @RequestBody String request) {

    //     String response = ssoService.test2(request);
        
    //     log.info("response to return", response);
    //     return ResponseEntity.ok(response);
    // }

    // Caso de prueba 3: SSO 
    
    // @PostMapping("/handoff")
    // public ResponseEntity<HandoffDTOResponse> handoff(
    //     @RequestHeader("Authorization") 
    //     String bearerTokenRequest,                             
    //     @RequestBody HandoffDTORequest request) {

    //     log.error("Endpoint /handoff activated");
    //     log.error("Bearer Token received: {}", bearerTokenRequest);

    //     HandoffDTOResponse response = ssoService.processHandoff(request.getApp(), request.getOtc(), bearerTokenRequest);
        
    //     log.error("response to return: {}", response);
    //     return ResponseEntity.ok(response);

    // }

    // -----------------------------------------------

    // @PostMapping("/handoff")
    public ResponseEntity<HandoffDTOResponse> handoff(
        @RequestHeader("Authorization") 
        String bearerTokenRequest,                             
        @RequestBody HandoffDTORequest request) {

        log.info("Endpoint /handoff activated");
        log.info("Bearer Token received", bearerTokenRequest);

        HandoffDTOResponse response = ssoService.processHandoff(request.getApp(), request.getOtc(), bearerTokenRequest);
        
        log.info("response to return", response);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/activateSession")
    public ResponseEntity<McsSSODTOResponse> activateSession(
        @RequestBody SessionTokenDTORequest request) {
        
        log.info("Endpoint /activateSession activated");
        log.info("sessionToken received: {}", request.getSessionToken());

        try {
            String jwt = ssoService.activateSession(request.getSessionToken());
            log.info("JWT generado correctamente: {}", jwt);
            
            McsSSODTOResponse response = new McsSSODTOResponse();
            response.setJwt(jwt);
            response.setMessage("Sesión activada correctamente");
            response.setSuccess(true);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
        
            log.error("Error al activar sesión del usuario: {}", e.getMessage());
            
            McsSSODTOResponse errorResponse = new McsSSODTOResponse();
            errorResponse.setJwt(null);
            errorResponse.setMessage("Error al activar la sesión"); // O puedes usar e.getMessage()
            errorResponse.setSuccess(false);
            
            return ResponseEntity.ok(errorResponse);
        }
    }

    // @PostMapping("/activateSession")
    // public void activateSession(
    //     @RequestBody String sessionToken, 
    //     HttpServletResponse response) {
        
    //     log.info("Endpoint /activateSession activated");
    //     log.info("sessionToken received", sessionToken);

    //     try {

    //         String jwt = ssoService.activateSession(sessionToken);
    //         log.info("JWT generado correctamente", jwt);

    //         Cookie cookie = new Cookie("token", jwt);
    //         cookie.setHttpOnly(false); 
    //         cookie.setPath("/");      
    //         cookie.setMaxAge(300000);   
    //         cookie.setSecure(true); 

    //         response.addCookie(cookie);

    //     } catch (RuntimeException e) {
    //         log.error("Error al activar sesión del usuario");
    //     }
    // }

    @CrossOrigin(origins = {
        "null" 
    })
    @PostMapping("/entrance")
    public void entrance(@RequestParam("sessionToken") String sessionToken, 
                        HttpServletResponse response) throws IOException {

        String appURL = parameterService.getValueByKey("allowed.origins"); 
        
        log.info("Endpoint /entrance activated");
        log.info("sessionToken received", sessionToken);

        try {

            String myJwt = ssoService.activateSession(sessionToken);

            Cookie cookie = new Cookie("token", myJwt);
            cookie.setHttpOnly(true); 
            cookie.setPath("/");      
            cookie.setMaxAge(300000);   
            cookie.setSecure(true); 

            response.addCookie(cookie);

            log.info("JWT generado correctamente", myJwt);

            // 3. Redirección exitosa al Frontend
            //response.sendRedirect(appURL.concat("/fincxp/"));
            log.info("redireccionando al website");
            //response.sendRedirect("https://uatbancaempresas.davivienda.com.sv/fincxp");
            response.sendRedirect("https://uatbancaempresas.davivienda.com.sv/financiamientocuentasporpagar/");

        } catch (RuntimeException e) {
            // 4. Manejo de errores: Redirigir a una página de error en React
            // e.getMessage() contendrá "invalid_session" o "user_not_found"
            log.info("Error al redireccionar al website");
            response.sendRedirect(appURL.concat("/fincxp") + e.getMessage());
        }
    }

}
