package com.davivienda.factoraje.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davivienda.factoraje.domain.dto.document.DocumentResponseDTO;
import com.davivienda.factoraje.domain.dto.termVersion.TermVersionDTOResponse;
import com.davivienda.factoraje.service.DocumentService;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    public DocumentController(){
    
    }

    // READ ALL BY AGREEMENT ID
    // @GetMapping
    // public ResponseEntity<List<DocumentResponseDTO>> getAllByEntityId(@PathVariable UUID id) {
    //     return ResponseEntity.ok().body(documentService.getAllDocumentsByAgreementId(id));
    // }
    
}
