package com.davivienda.factoraje.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.davivienda.factoraje.domain.model.LogDocumentModel;
import com.davivienda.factoraje.repository.LogDocumentRepository;

@Service
public class AuditLogService {

    @Autowired
    private LogDocumentRepository logDocumentRepository;

    public void logAction(UUID documentId, UUID userId, String action) {
        if (documentId == null || userId == null || action == null) {
            throw new IllegalArgumentException("Argumentos no pueden ser null");
        }
        LogDocumentModel log = new LogDocumentModel(documentId, userId, action);
        logDocumentRepository.save(log);
    }
}