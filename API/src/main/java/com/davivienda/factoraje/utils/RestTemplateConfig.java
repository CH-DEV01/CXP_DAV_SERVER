package com.davivienda.factoraje.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        
        factory.setConnectTimeout(5000); // 5 segundos para intentar conectar
        factory.setReadTimeout(10000);   // 10 segundos esperando la respuesta

        return new RestTemplate(factory);
    }
}