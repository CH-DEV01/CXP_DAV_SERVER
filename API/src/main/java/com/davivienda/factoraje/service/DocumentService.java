package com.davivienda.factoraje.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.davivienda.factoraje.domain.dto.document.DocumentResponseDTO;
import com.davivienda.factoraje.domain.model.AgreementModel;
import com.davivienda.factoraje.domain.model.DocumentModel;
import com.davivienda.factoraje.exception.ResourceNotFoundException;
import com.davivienda.factoraje.repository.DocumentRepository;

@Service
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);
    private final DocumentRepository documentRepository;
    private final EntityService entityService;

    public DocumentService(DocumentRepository documentRepository, EntityService entityService) {
        this.documentRepository = documentRepository;
        this.entityService = entityService;
        log.info("DocumentService initialized");
    }

    public DocumentModel createDocument(DocumentModel document) {
        // if (document == null) {
        //     log.warn("createDocument called with null document");
        //     throw new IllegalArgumentException("El documento no puede ser nulo");
        // }

        if (document == null) {
            log.warn("createDocument called with null document");
            throw new IllegalArgumentException("El documento no puede ser nulo");
        }
        log.debug("Saving document with number {}", document.getDocumentNumber());
        return documentRepository.save(document);
    }

    public Optional<DocumentModel> getDocumentById(UUID documentId){

        return documentRepository.findById(documentId);

    }

    // public void deleteDocument(UUID id){
    //     documentRepository.deleteById(id);
    // }

    public void deleteDocument(UUID id){
        if (!documentRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Documento con id " + id + " no encontrado.");
        }
        documentRepository.deleteById(id);
    }

    public Optional<DocumentModel> getDocumentByNumber(String documentNumber){
        return documentRepository.findByDocumentNumber(documentNumber);
    }

    public List<DocumentModel> getAllDocuments() {
        log.debug("Fetching all documents");
        List<DocumentModel> docs = documentRepository.findAll();
        if (docs == null || docs.isEmpty()) {
            log.info("No documents found in database");
        } else {
            log.info("Found {} documents", docs.size());
        }
        return docs;
    }

    // public List<DocumentResponseDTO> getAllDocumentsByAgreementId(UUID id){

    //     AgreementModel agreement = agreementService.findById(id);
        
    //     List<DocumentModel> documents = agreement.getDocuments();

    //     List<DocumentResponseDTO> response = documents.stream()
    //         .map(document -> {
    //             DocumentResponseDTO dto = new DocumentResponseDTO();
    //             dto.setSupplierName(document.getSupplierName());
    //             dto.setDocumentId(document.getDocument_id());
    //             dto.setAmount(document.getAmountToFinance());
    //             dto.setCommission(document.getCommission());
    //             dto.setDisbursementDate(document.getDisbursementDate());
    //             dto.setDocumentNumber(document.getDocumentNumber());
    //             dto.setIssueDate(document.getIssueDate());
    //             dto.setStatus(document.getStatus());
    //             dto.setId(document.getDocument_id());
    //             dto.setAmountToFinance(document.getAmountToFinance());
        
    //             return dto;
    //         })
    //         .collect(Collectors.toList());
            
    //     return response;
    // }
}
