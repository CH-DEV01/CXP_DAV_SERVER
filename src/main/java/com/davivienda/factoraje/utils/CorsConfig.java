package com.davivienda.factoraje.utils;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.davivienda.factoraje.service.AgreementService;
import com.davivienda.factoraje.service.ParameterService;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final ParameterService parameterService; 
    private static final Logger log = LoggerFactory.getLogger(AgreementService.class);

    public CorsConfig(ParameterService parameterService){
        this.parameterService = parameterService;
    }

    private static final String PARAM_KEY_ALLOWED_ORIGINS = "allowed.origins";

    private Optional<String> getParameter(String key) {
        try {
            String value = parameterService.getValueByKey(key);
            return Optional.ofNullable(value); 
        } catch (RuntimeException e) {
            log.warn("No se pudo obtener el parámetro '{}'.", key, e); 
            return Optional.empty();
        }
    }

    // @Override
    // public void addCorsMappings(CorsRegistry registry) {
    //     registry.addMapping("/**")
    //             .allowedOrigins(getParameter(PARAM_KEY_ALLOWED_ORIGINS).get())
    //             .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    //             .allowedHeaders("*")
    //             .allowCredentials(true);
    // }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
