package com.davivienda.factoraje.controller;
import com.davivienda.factoraje.service.ParameterService;
import com.davivienda.factoraje.service.SsoService;
import com.davivienda.factoraje.domain.dto.sso.HandoffDTORequest;
import com.davivienda.factoraje.domain.dto.sso.HandoffDTOResponse;
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

    public SsoController(SsoService ssoService, ParameterService parameterService) {
        this.ssoService = ssoService;
        this.parameterService = parameterService;
    }

    @PostMapping("/handoff")
    public ResponseEntity<HandoffDTOResponse> handoff(
        @RequestHeader("Authorization") 
        String authHeader,                             
        @RequestBody HandoffDTORequest request) {

        HandoffDTOResponse response = ssoService.processHandoff(request.getApp(), request.getOtc(), authHeader);
            
        return ResponseEntity.ok(response);

    }

    @CrossOrigin(origins = {
        "null" 
    })
    @PostMapping("/entrance")
    public void entrance(@RequestParam("sessionToken") String sessionToken, 
                        HttpServletResponse response) throws IOException {

        String appURL = parameterService.getValueByKey("allowed.origins"); 

        try {

            String myJwt = ssoService.activateSession(sessionToken);

            Cookie cookie = new Cookie("token", myJwt);
            cookie.setHttpOnly(true); // Evita XSS en React - true solo en producción
            cookie.setPath("/");      // Disponible en toda la app
            cookie.setMaxAge(300000);   
            cookie.setSecure(true); // IMPORTANTE: Descomentar en producción (HTTPS)

            response.addCookie(cookie);

            // 3. Redirección exitosa al Frontend
            response.sendRedirect(appURL.concat("/fincxp/"));

        } catch (RuntimeException e) {
            // 4. Manejo de errores: Redirigir a una página de error en React
            // e.getMessage() contendrá "invalid_session" o "user_not_found"
            response.sendRedirect(appURL.concat("/fincxp") + e.getMessage());
        }
    }

}
