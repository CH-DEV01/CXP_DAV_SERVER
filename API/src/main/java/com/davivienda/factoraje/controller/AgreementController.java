package com.davivienda.factoraje.controller;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davivienda.factoraje.domain.dto.agreement.BulkUpdateRequestDTO;
import com.davivienda.factoraje.domain.dto.document.UpdateDocumentsRequestDTO;
import com.davivienda.factoraje.domain.model.AgreementModel;
import com.davivienda.factoraje.service.AgreementService;
import com.davivienda.factoraje.auth.PermissionsAllowed;
import com.davivienda.factoraje.auth.RolesAllowed;

@RestController
@RequestMapping("/api/agreement")
public class AgreementController {

    private final AgreementService agreementService;
    private static final Logger log = LoggerFactory.getLogger(AgreementController.class);

    public AgreementController(AgreementService agreementService) {
        this.agreementService = agreementService;
        log.info("AgreementController initialized");
    }

    @RolesAllowed({ "MANAGER" })
    @PermissionsAllowed({"agreement_management_view"})
    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        log.info("GET /api/agreement/getAll - fetching all agreements");
        List<AgreementModel> list = agreementService.findAll();
        log.info("Found {} agreements", list.size());
        return ResponseEntity.ok(list);
    }

    @RolesAllowed({"AUTHORIZING", "AUTHORIZING_TWO_MODE_AUTH"})
    @PermissionsAllowed({"approve_documents_two_mode_auth_execute", "approve_documents_execute"})
    @GetMapping("/byPayer/{payerId}")
    public ResponseEntity<?> getByPayer(@PathVariable String payerId) {
        log.info("GET /api/agreement/byPayer/{} - fetch agreements by payer", payerId);
        if (payerId == null || payerId.trim().isEmpty()) {
            log.warn("payerId is null or empty");
            return ResponseEntity.badRequest()
                    .body("El parámetro 'payerId' no puede ser vacío");
        }
 
        List<AgreementModel> agreements = agreementService.findByPayer(payerId);
        if (agreements.isEmpty()) {
            log.info("No agreements found for payer {}", payerId);
            return ResponseEntity.noContent().build();
        }
        log.info("Found {} agreements for payer {}", agreements.size(), payerId);
        return ResponseEntity.ok(agreements);
    }



    //@RolesAllowed({"SUPPLIER", "SUPPLIER_TWO_MODE_AUTH", "MANAGER"})
    //@PermissionsAllowed({"select_documents_execute_two_mode_auth", "select_documents_execute"})
    @GetMapping("/bySupplier/{supplierId}")
    public ResponseEntity<?> getBySupplier(@PathVariable String supplierId) {
        log.info("GET /api/agreement/bySupplier/{} - fetch agreements by supplier", supplierId);
        if (supplierId == null || supplierId.trim().isEmpty()) {
            log.warn("supplierId is null or empty");
            return ResponseEntity.badRequest()
                    .body("El parámetro 'supplierId' no puede ser vacío");
        }

        List<AgreementModel> agreements = agreementService.findBySupplier(supplierId);
        if (agreements.isEmpty()) {
            log.info("No agreements found for supplier {}", supplierId);
            return ResponseEntity.noContent().build();
        }
        log.info("Found {} agreements for supplier {}", agreements.size(), supplierId);
        return ResponseEntity.ok(agreements);
    }

    @PostMapping("/bulk-approve")
    public ResponseEntity<Void> approveDocumentsInBulk(@RequestBody BulkUpdateRequestDTO bulkRequest) {
        agreementService.approveDocumentsInBulk(bulkRequest);
        return ResponseEntity.ok().build();
    }

    @RolesAllowed({"AUTHORIZING", "AUTHORIZING_TWO_MODE_AUTH", "SUPPLIER", "SUPPLIER_TWO_MODE_AUTH"})
    @PostMapping("/updateDocuments/{agreementId}/{status}/{payerId}/{authMode}/{userId}")
    public ResponseEntity<?> updateDocuments(
            @PathVariable String agreementId,
            @PathVariable String status,
            @PathVariable String payerId,
            @PathVariable Integer authMode,
            @PathVariable String userId,
            @RequestBody UpdateDocumentsRequestDTO documentIds) {
        log.info("POST /api/agreement/updateDocuments/{}/{} - update documents: {}", agreementId, status, documentIds);
        if (agreementId == null || agreementId.trim().isEmpty() || status == null || status.trim().isEmpty()) {
            log.warn("agreementId or status is null/empty");
            return ResponseEntity.badRequest()
                    .body("Los parámetros 'agreementId' y 'status' no pueden ser vacíos");
        }

        AgreementModel updated = agreementService.updateDocuments(agreementId, status, documentIds, payerId, authMode, userId);
        log.info("Documents updated for agreement {} with status {}", agreementId, status);
        return ResponseEntity.ok(updated);
        
    }

    @RolesAllowed({"SUPPLIER", "SUPPLIER_TWO_MODE_AUTH"})
    @PermissionsAllowed({"select_documents_execute_two_mode_auth", "select_documents_execute"})
    @GetMapping("/byId/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        log.info("GET /api/agreement/byId/{} - fetch agreement by id", id);
        if (id == null) {
            log.warn("id is null");
            return ResponseEntity.badRequest()
                    .body("El parámetro 'id' no puede ser nulo");
        }

        AgreementModel agreement = agreementService.findById(id);
        if (agreement == null) {
            log.info("No agreement found for id {}", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Agreement {} found", id);
        return ResponseEntity.ok(agreement);
    }

    @RolesAllowed({"MANAGER"})
    @PermissionsAllowed({ "delete_documents" })
    @DeleteMapping("/deleteDocument/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String documentId) {
        agreementService.deleteDocument(documentId);
            return ResponseEntity.noContent().build();
    }

}
