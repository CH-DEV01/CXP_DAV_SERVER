package com.davivienda.factoraje.auth;

import com.davivienda.factoraje.domain.model.PermissionModel;
import com.davivienda.factoraje.domain.model.RoleModel;
import com.davivienda.factoraje.domain.model.UserModel;
import com.davivienda.factoraje.service.UserService;
import com.davivienda.factoraje.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthorizationInterceptor implements HandlerInterceptor {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod hm = (HandlerMethod) handler;

        String token = extractToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                               "Token inválido o expirado");
            return false;
        }

        String dui = jwtUtil.getDUIFromToken(token);
        UserModel user = userService.findUserByDUI(dui);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                               "Usuario no encontrado");
            return false;
        }

        RoleModel role = user.getRole();            
        String roleName = role.getRoleName();
        Set<String> userPermissions = role.getPermissions().stream()
                                        .map(PermissionModel::getPermissionName)
                                        .collect(Collectors.toSet());          

        if (!isAuthorized(hm, roleName, userPermissions)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                               "Acceso denegado");
            return false;
        }

        request.setAttribute("loggedUser", user);
        return true;
    }

    private String extractToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        return Arrays.stream(cookies)
                     .filter(c -> "token".equals(c.getName()))
                     .map(Cookie::getValue)
                     .findFirst()
                     .orElse(null);
    }

    private boolean isAuthorized(HandlerMethod hm, String userRole, Set<String> userPermissions) {
        // Busca ambas anotaciones
        RolesAllowed ra = hm.getMethodAnnotation(RolesAllowed.class);
        if (ra == null) {
            ra = hm.getBeanType().getAnnotation(RolesAllowed.class);
        }

        PermissionsAllowed pa = hm.getMethodAnnotation(PermissionsAllowed.class);
        if (pa == null) {
            pa = hm.getBeanType().getAnnotation(PermissionsAllowed.class);
        }
        
        // Si no hay ninguna anotación de seguridad, se permite el acceso.
        if (ra == null && pa == null) {
            return true;
        }

        // Comprobación de roles
        boolean rolesOk = false;
        if (ra != null) {
            // <-- CAMBIO CLAVE: Se valida el String del rol del usuario contra la lista de roles permitidos
            rolesOk = Arrays.stream(ra.value()).anyMatch(userRole::equals);
        } else {
            rolesOk = true; // Si no se requieren roles, esta condición está OK
        }

        // Comprobación de permisos (sin cambios en esta parte)
        boolean permissionsOk = false;
        if (pa != null) {
            permissionsOk = Arrays.stream(pa.value()).anyMatch(userPermissions::contains);
        } else {
            permissionsOk = true; // Si no se requieren permisos, esta condición está OK
        }

        // El usuario debe cumplir AMBAS condiciones para acceder
        return rolesOk && permissionsOk;
    }
}