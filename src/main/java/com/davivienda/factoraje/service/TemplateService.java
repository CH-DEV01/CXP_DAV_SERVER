package com.davivienda.factoraje.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class TemplateService {

    @Autowired
    private ResourceLoader resourceLoader;

    public String loadHTMLTemplate(String fileName) {
        Resource resource = resourceLoader.getResource("classpath:templates/emails/" + fileName);
        
        try (InputStream inputStream = resource.getInputStream()) {
            
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            
            byte[] buffer = new byte[1024];
            int length;
            
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            
            return new String(result.toByteArray(), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException("Error al cargar la plantilla: " + fileName, e);
        }
    }
}
