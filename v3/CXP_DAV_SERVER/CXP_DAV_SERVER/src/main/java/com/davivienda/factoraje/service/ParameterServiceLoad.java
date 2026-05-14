package com.davivienda.factoraje.service;

import java.util.Collections;                 
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.davivienda.factoraje.domain.model.ParameterModel;
import com.davivienda.factoraje.repository.ParameterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParameterServiceLoad implements InitializingBean {

    private final ParameterRepository repository;

    public static String INTEREST_RATE;
    public static String BASE;
    public static String COMISSION;
    
    public static String FABRICA_ENTORNO;
    public static String SERVICIO_ENTORNO;

    public static String MAILJET_API_KEY;
    public static String MAILJET_API_SECRET;
    public static String MAILJET_EMAIL_MANAGER_BANK;
    public static String MAILJET_EMAIL_SUBJECT;

    private Map<String, String> cache = Collections.emptyMap();

    @Override
    @Transactional(readOnly = true)
    public void afterPropertiesSet() {

        cache = repository.findAll().stream()
                .collect(Collectors.toMap(
                        ParameterModel::getKey,
                        ParameterModel::getValue
                ));

        INTEREST_RATE = cache.get("param.key.interest");
        BASE  = cache.get("param.key.base");
        COMISSION = cache.get("param.key.comission");
        FABRICA_ENTORNO = cache.get("fabricaEntorno");
        SERVICIO_ENTORNO = cache.get("servicioEntorno");
        MAILJET_API_KEY = cache.get("mailjet.api.key");
        MAILJET_API_KEY = cache.get("mailjet.api.secret");
        MAILJET_EMAIL_MANAGER_BANK = cache.get("mailjet.email.manager.bank");
        MAILJET_EMAIL_SUBJECT = cache.get("mailjet.email.sender.subject");
        
    }

    public String get(String key) {
        return cache.get(key);
    }
}