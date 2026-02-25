package com.davivienda.factoraje.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;     
import javax.transaction.Transactional;

import org.apache.commons.compress.harmony.pack200.BandSet.BandAnalysisResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.davivienda.factoraje.domain.dto.Documents.UpdateDocumentsRequestDTO;
import com.davivienda.factoraje.domain.dto.Emails.DestinatarioRequestDTO;
import com.davivienda.factoraje.domain.dto.Emails.EmailRequestDTO;
import com.davivienda.factoraje.domain.dto.Emails.HTMLVariablesDTO;
import com.davivienda.factoraje.domain.dto.agreement.ApprovalRequestDTO;
import com.davivienda.factoraje.domain.dto.agreement.BulkUpdateRequestDTO;
import com.davivienda.factoraje.domain.model.AgreementModel;
import com.davivienda.factoraje.domain.model.DocumentModel;
import com.davivienda.factoraje.domain.model.EntityModel;
import com.davivienda.factoraje.domain.model.UserModel;
import com.davivienda.factoraje.event.EmailEvent;
import com.davivienda.factoraje.exception.ResourceAlreadyExistsException;
import com.davivienda.factoraje.exception.ResourceNotFoundException;
import com.davivienda.factoraje.repository.AgreementRepository;

import java.util.Collections;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class AgreementService {

    private static final Logger log = LoggerFactory.getLogger(AgreementService.class);
    private final ApplicationEventPublisher applicationEventPublisher;
    private final EntityService entityService;
    private final AgreementRepository agreementRepository;
    private final DocumentService documentService;
    private final ParameterService parameterService;
    private final UserService userService;
    private final AuditLogService auditLogService;

    private static final String PARAM_KEY_EMAIL_MANAGER_BANK = "mailjet.email.manager.bank";
    private static final String PARAM_KEY_NAME_MANAGER_BANK = "mailjet.name.manager.bank";

    public AgreementService(AuditLogService auditLogService, UserService userService, ParameterService parameterService, AgreementRepository agreementRepository, ApplicationEventPublisher applicationEventPublisher, EntityService entityService, DocumentService documentService) {
        this.agreementRepository = agreementRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.entityService = entityService;
        this.documentService = documentService;
        this.parameterService = parameterService;
        this.userService = userService;
        this.auditLogService = auditLogService;
        log.info("AgreementService initialized");
    }

    private Optional<String> getParameter(String key) {
        try {
            String value = parameterService.getValueByKey(key);
            return Optional.ofNullable(value); 
        } catch (ResourceNotFoundException e) {
            log.warn("No se pudo obtener el parámetro '{}'.", key, e); 
            return Optional.empty();
        }
    }

    public List<AgreementModel> findAll() {
        log.debug("Fetching all agreements");
        return agreementRepository.findAll();
    }

    public AgreementModel findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("El parámetro 'id' no puede ser nulo");
        }
        return agreementRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Acuerdo no encontrado con id=" + id));
    }

    public AgreementModel findByIdentifier(String code) {
        
        if (code == null || code.trim().isEmpty()) {
            log.warn("findByIdentifier called with empty code");
            throw new IllegalArgumentException("El parámetro 'código' no puede ser nulo");
        }
        
        AgreementModel agreement = agreementRepository.findByIdentifier(code);

        if (agreement == null) {
            return null;
            //throw new ResourceNotFoundException("Acuerdo no encontrado con código=" + code);
        }
        
        return agreement;
    }

    public AgreementModel save(AgreementModel agreement) {
        if (agreement == null) {
            throw new IllegalArgumentException("El objeto 'acuerdo' no puede ser nulo");
        }
        log.debug("Saving new agreement with identifier {}", agreement.getIdentifier());
        return agreementRepository.save(agreement);
    }

    public List<AgreementModel> findByPayer(String payerId) {
        if (payerId == null || payerId.trim().isEmpty()) {
            log.warn("findByPayer called with empty payerId");
            throw new IllegalArgumentException("El parámetro 'payerId' no puede ser nulo");
        }
        log.debug("Filtering agreements by payerId {}", payerId);
        return agreementRepository.findAll().stream()
                .filter(a -> a.getIdentifier() != null &&
                        a.getIdentifier().length() >= 36 &&
                        a.getIdentifier().substring(0, 36).equals(payerId))
                .collect(Collectors.toList());
    }

    public List<AgreementModel> findByPayerWithStatus(String payerId, String status) {
        if (payerId == null || status == null) {
            throw new IllegalArgumentException("El parámetro 'payerId' no puede ser nulo");
        }

        List<AgreementModel> agreements = agreementRepository.findAll().stream()
                .filter(agreement -> {
                    String identifier = agreement.getIdentifier();
                    return identifier != null &&
                            identifier.length() >= 36 &&
                            identifier.substring(0, 36).equals(payerId);
                })
                .collect(Collectors.toList());

        for (AgreementModel agreement : agreements) {
            List<DocumentModel> documents = new ArrayList<>();
            for (DocumentModel document : agreement.getDocuments()) {
                if (document.getStatus() != null && document.getStatus().equals(status)) {
                    documents.add(document);
                }
            }
            agreement.setDocuments(documents);
        }

        return agreements;
    }

    public List<AgreementModel> findBySupplierWithStatus(String supplierId, String status) {
        if (supplierId == null || status == null) {
            throw new IllegalArgumentException("El parámetro 'supplierId' no puede ser nulo");
        }

        List<AgreementModel> agreements = agreementRepository.findAll().stream()
                .filter(agreement -> {
                    String identifier = agreement.getIdentifier();
                    return identifier != null &&
                            identifier.length() >= 36 &&
                            identifier.substring(36).equals(supplierId);
                })
                .collect(Collectors.toList());

        for (AgreementModel agreement : agreements) {
            List<DocumentModel> documents = new ArrayList<>();
            for (DocumentModel document : agreement.getDocuments()) {
                if (document.getStatus() != null && document.getStatus().equals(status)) {
                    documents.add(document);
                }
            }
            agreement.setDocuments(documents);
        }

        return agreements;
    }


    @Transactional
    public void approveDocumentsInBulk(BulkUpdateRequestDTO bulkRequest) {

        // 1. Validaciones iniciales
        if (bulkRequest.getApprovals() == null || bulkRequest.getApprovals().isEmpty()) {
            throw new IllegalArgumentException("La lista de aprobaciones no puede estar vacía.");
        }
        if (!"APPROVED".equals(bulkRequest.getStatus())) {
             throw new IllegalArgumentException("Esta operación masiva solo permite el estado 'APPROVED'.");
        }

        // 2. Obtener datos comunes una sola vez
        UserModel approver = userService.getUserById(bulkRequest.getUserId());
        
        if(approver == null){
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

        // 3. Inicializar acumuladores para los totales generales
        BigDecimal grandTotalToFinance = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal grandTotalCommission = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        // 4. Iterar y procesar cada grupo de aprobación
        for (ApprovalRequestDTO approval : bulkRequest.getApprovals()) {
            UUID agreementId = approval.getAgreementId();
            List<UUID> documentIds = approval.getDocumentIds();

            AgreementModel agreement = agreementRepository.findById(agreementId)
                    .orElseThrow(() -> new ResourceNotFoundException("Convenio con id=" + agreementId + " no encontrado durante la operación masiva."));

            List<DocumentModel> docsToUpdate = agreement.getDocuments().stream()
                .filter(doc -> documentIds.contains(doc.getDocument_id()))
                .collect(Collectors.toList());

            // Actualizar estado y responsable
            docsToUpdate.forEach(doc -> {
                doc.setStatus(bulkRequest.getStatus());
                doc.setApprovedBy(approver);
                auditLogService.logAction(doc.getDocument_id(), bulkRequest.getUserId(), "APPROVED");
            });

            // Acumular los montos en los totales generales
            for (DocumentModel doc : docsToUpdate) {
                if (doc.getAmountToFinance() != null) {
                    grandTotalToFinance = grandTotalToFinance.add(doc.getAmountToFinance());
                }
                if (doc.getCommission() != null) {
                    grandTotalCommission = grandTotalCommission.add(doc.getCommission());
                }
            }

            // Guardar el convenio modificado
            agreementRepository.save(agreement);
            log.info("Convenio {} procesado en la operación masiva.", agreementId);
        }

        // 5. Enviar una única notificación consolidada al final
        sendAggregateApprovalNotification(grandTotalToFinance, grandTotalCommission, bulkRequest);
        log.info("Operación masiva completada. Total a financiar: {}. Total comisión: {}", grandTotalToFinance, grandTotalCommission);
    }

    private void sendAggregateApprovalNotification(BigDecimal totalFinance, BigDecimal totalCommission, BulkUpdateRequestDTO request) {
        log.debug("Preparando notificación agregada para el pagador {}", request.getPayerId());

        EntityModel payer = entityService.getEntityById(request.getPayerId());

        if(payer == null){
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

         String nameManagerBank = getParameter(PARAM_KEY_NAME_MANAGER_BANK)
                .orElseThrow(() -> new ResourceNotFoundException("Parámetro 'nameManagerBank' no configurado."));

        String emailManagerBank = getParameter(PARAM_KEY_EMAIL_MANAGER_BANK)
                .orElseThrow(() -> new ResourceNotFoundException("Parámetro 'emailManagerBank' no configurado."));

        EmailRequestDTO emailRequest = new EmailRequestDTO();
        HTMLVariablesDTO variables = new HTMLVariablesDTO();
        DestinatarioRequestDTO bankRecipient = new DestinatarioRequestDTO();
        bankRecipient.setName(nameManagerBank);
        bankRecipient.setEmail(emailManagerBank);

        variables.setNombreEmpresa(payer.getName());
        variables.setNombreProveedor("");
        variables.setNumeroCuentaProveedor("");
        variables.setNumeroLineaCredito(payer.getCreditLineNumber());
        variables.setNIT(payer.getNit());
        variables.setDistrito(payer.getDistrict());
        variables.setDepartamento(payer.getDepartment());
        variables.setMunicipio(payer.getMunicipality());
        variables.setNumeroCuentaPagador(payer.getAccountBank());
        variables.setMontoDesembolsar(totalFinance);
        variables.setComission(totalCommission);

        List<DestinatarioRequestDTO> destinos = new ArrayList<>();
        destinos.add(bankRecipient);

        emailRequest.setDestinatarios(destinos);
        emailRequest.setHtmlVariables(variables);
        
        if (request.getAuthMode() == 1) {
            emailRequest.setTipoHtml(1);
        } else if (request.getAuthMode() == 2) {
            emailRequest.setTipoHtml(4);
        } else {
            log.warn("authMode no reconocido: {}. No se enviará correo.", request.getAuthMode());
            return;
        }

        applicationEventPublisher.publishEvent(new EmailEvent(emailRequest));
        log.info("Notificación agregada enviada al banco para la operación del pagador {}", request.getPayerId());
    }

    @Transactional
    public AgreementModel updateDocuments(
            String agreementId, 
            String status,
            UpdateDocumentsRequestDTO documentIds, 
            String payerId,
            Integer authMode,
            String userId) {

        if (documentIds == null || documentIds.getDocumentIds() == null || documentIds.getDocumentIds().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar al menos un documento a actualizar.");
        }

        String nameManagerBank = getParameter(PARAM_KEY_NAME_MANAGER_BANK)
                .orElseThrow(() -> new ResourceNotFoundException("Parámetro 'nameManagerBank' no configurado."));

        String emailManagerBank = getParameter(PARAM_KEY_EMAIL_MANAGER_BANK)
                .orElseThrow(() -> new ResourceNotFoundException("Parámetro 'emailManagerBank' no configurado."));

        BigDecimal toFinanceTotalAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal commissionTotal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        for (UUID docId : documentIds.getDocumentIds()) {

            DocumentModel doc = documentService.getDocumentById(docId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento con id=" + docId + " no encontrado."));

            if (doc.getAmountToFinance() != null) {
                toFinanceTotalAmount = toFinanceTotalAmount.add(doc.getAmountToFinance());
            }
            if (doc.getCommission() != null) {
                commissionTotal = commissionTotal.add(doc.getCommission());
            }
        }

        if (agreementId == null || agreementId.trim().isEmpty()) {
            log.warn("updateDocuments called with empty agreementId");
            throw new IllegalArgumentException("El parámetro 'agreementId' no puede ser vacío");
        }
        if (status == null || status.trim().isEmpty()) {
            log.warn("updateDocuments called with empty status");
            throw new IllegalArgumentException("El parámetro 'status' no puede ser vacío");
        }
        if (documentIds == null || documentIds.getDocumentIds() == null
                || documentIds.getDocumentIds().isEmpty()) {
            log.warn("updateDocuments called with no document IDs");
            throw new IllegalArgumentException("Debe especificar al menos un documento a actualizar");
        }

        UUID id;
        try {
            id = UUID.fromString(agreementId);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format for agreementId {}", agreementId);
            throw new IllegalArgumentException("El parámetro 'agreementId' debe ser un UUID válido");
        }

        //AgreementModel agreement = agreementRepository.findById(id).get();
        AgreementModel agreement = this.findById(id); // Llama a tu método seguro

        if (agreement == null) {
            throw new ResourceNotFoundException("Usuario con id=" + agreementId + " no encontrado.");
        }

        log.debug("Updating {} documents to status {} for agreement {}",
                documentIds.getDocumentIds().size(), status, agreementId);

        UUID approvedByUserId = UUID.fromString(userId);
        UserModel approvedOrSelectedBy = userService.getUserById(approvedByUserId);
        if (approvedOrSelectedBy == null) {
            throw new ResourceNotFoundException("Usuario con id=" + approvedByUserId + " no encontrado.");
        }

        // Cambiar estos estados a constantes
        Optional.ofNullable(agreement.getDocuments()).orElse(Collections.emptyList()).forEach(doc -> {
            if (documentIds.getDocumentIds().contains(doc.getDocument_id())) {    
                doc.setStatus(status);
                if(status.equals("SELECTED")){
                    doc.setSelectedBy(approvedOrSelectedBy);
                    //auditLogService.logAction(doc.getDocument_id(), approvedByUserId, "SELECTED");
                }
                
                if(status.equals("APPROVED")){
                    doc.setApprovedBy(approvedOrSelectedBy);
                    //auditLogService.logAction(doc.getDocument_id(), approvedByUserId, "APPROVED");
                }
            }
        });

        switch (authMode) {
            case 1:
                if(status.equals("SELECTED")){
                    // Correo al pagador
                    UUID payerIdToSend;
                    try {
                        payerIdToSend = UUID.fromString(payerId);
                    } catch (IllegalArgumentException e) {
                        log.warn("Invalid UUID format for payerId {}", payerId);
                        throw new IllegalArgumentException("El parámetro 'agreementId' debe ser un UUID válido");
                    }

                    EntityModel payer = new EntityModel();
                    payer = entityService.getEntityById(payerIdToSend);
                    if (payer == null) {
                        throw new ResourceNotFoundException("Entidad pagadora con id=" + payerIdToSend + " no encontrada.");
                    }

                    EmailRequestDTO request = new EmailRequestDTO();
                    HTMLVariablesDTO payerVariables = new HTMLVariablesDTO();

                    DestinatarioRequestDTO destinoPagador = new DestinatarioRequestDTO();
                    destinoPagador.setName(payer.getName());
                    destinoPagador.setEmail(payer.getEmail());

                    List<DestinatarioRequestDTO> destinos = new ArrayList<>();
                    destinos.add(destinoPagador);

                    payerVariables.setNombreEmpresa(payer.getName());
                    payerVariables.setNombreProveedor("");
                    payerVariables.setNumeroCuentaProveedor("");
                    payerVariables.setNumeroLineaCredito("");
                    payerVariables.setMontoDesembolsar(BigDecimal.ZERO);

                    request.setTipoHtml(3);
                    request.setDestinatarios(destinos);
                    request.setHtmlVariables(payerVariables);

                    EmailEvent evt = new EmailEvent(request);
                    applicationEventPublisher.publishEvent(evt);
                } 

                if(status.equals("APPROVED")){
                    //Correo al banco
                    UUID payerIdToSend;
                    payerIdToSend = UUID.fromString(payerId);

                    EntityModel payer = new EntityModel();
                    payer = entityService.getEntityById(payerIdToSend);

                    if (payer == null) {
                        throw new ResourceNotFoundException("Entidad pagadora con id=" + payerIdToSend + " no encontrada.");
                    }

                    EmailRequestDTO request = new EmailRequestDTO();
                    HTMLVariablesDTO bankVariables = new HTMLVariablesDTO();

                    DestinatarioRequestDTO bank = new DestinatarioRequestDTO();
                    bank.setName(nameManagerBank);
                    bank.setEmail(emailManagerBank);

                    List<DestinatarioRequestDTO> destinos = new ArrayList<>();
                    destinos.add(bank);

                    bankVariables.setNombreEmpresa(payer.getName());
                    bankVariables.setNombreProveedor("");
                    bankVariables.setNumeroCuentaProveedor("");
                    bankVariables.setNumeroLineaCredito(payer.getCreditLineNumber());
                    bankVariables.setMontoDesembolsar(toFinanceTotalAmount);
                    bankVariables.setComission(commissionTotal);
                    bankVariables.setNIT(payer.getNit());
                    bankVariables.setDistrito(payer.getDistrict());
                    bankVariables.setDepartamento(payer.getDepartment());
                    bankVariables.setMunicipio(payer.getMunicipality());
                    bankVariables.setNumeroCuentaPagador(payer.getAccountBank());

                    request.setTipoHtml(1);
                    request.setDestinatarios(destinos);
                    request.setHtmlVariables(bankVariables);

                    EmailEvent evt = new EmailEvent(request);
                    applicationEventPublisher.publishEvent(evt);
                }
                break;
            
            case 2:

                // Correo al banco
                EmailRequestDTO requestAuthTwo = new EmailRequestDTO();
                HTMLVariablesDTO bankVariablesAuthTwo = new HTMLVariablesDTO();

                DestinatarioRequestDTO bankAuthTwo = new DestinatarioRequestDTO();
                bankAuthTwo.setName(nameManagerBank);
                bankAuthTwo.setEmail(emailManagerBank);

                List<DestinatarioRequestDTO> destinosAuthTwo = new ArrayList<>();
                destinosAuthTwo.add(bankAuthTwo);

                bankVariablesAuthTwo.setNombreEmpresa("");
                bankVariablesAuthTwo.setNombreProveedor("");
                bankVariablesAuthTwo.setNumeroCuentaProveedor("");
                bankVariablesAuthTwo.setNumeroLineaCredito("");
                bankVariablesAuthTwo.setMontoDesembolsar(toFinanceTotalAmount);
                bankVariablesAuthTwo.setComission(commissionTotal);

                requestAuthTwo.setTipoHtml(4);
                requestAuthTwo.setDestinatarios(destinosAuthTwo);
                requestAuthTwo.setHtmlVariables(bankVariablesAuthTwo);

                EmailEvent evtAuthTwo = new EmailEvent(requestAuthTwo);
                applicationEventPublisher.publishEvent(evtAuthTwo);

                break;

            default:
                break;
        }

        return agreementRepository.save(agreement);
    }

    public List<AgreementModel> findBySupplier(String supplierId) {
        if (supplierId == null) {
            throw new IllegalArgumentException("payerId cannot be null");
        }

        return agreementRepository.findAll().stream()
                .filter(agreement -> {
                    String identifier = agreement.getIdentifier();
                    return identifier != null &&
                            identifier.length() >= 36 &&
                            identifier.substring(36).equals(supplierId);
                })
                .collect(Collectors.toList());
    }

    public AgreementModel findByIdAndStatus(UUID id, String status) {
        if (id == null) {
            log.warn("findByIdAndStatus called with null id");
            throw new IllegalArgumentException("El parámetro 'id' no puede ser nulo");
        }
        if (status == null || status.trim().isEmpty()) {
            log.warn("findByIdAndStatus called with empty status");
            throw new IllegalArgumentException("El parámetro 'status' no puede ser vacío");
        }

        // AgreementModel agreement = agreementRepository.findById(id)
        //         .orElseThrow(() -> new ResourceNotFoundException(
        //                 "Acuerdo no encontrado con id=" + id));

        AgreementModel agreement = this.findById(id);

        log.debug("Filtering documents for agreement {} by status {}", id, status);

        // List<DocumentModel> filtered = agreement.getDocuments().stream()
        //         .filter(d -> status.equals(d.getStatus()))
        //         .collect(Collectors.toList());

        List<DocumentModel> filtered = Optional.ofNullable(agreement.getDocuments())
                .orElse(Collections.emptyList())
                .stream()
                .filter(d -> status.equals(d.getStatus()))
                .collect(Collectors.toList());

        agreement.setDocuments(filtered);
        return agreement;
    }

    @Transactional
    public void deleteDocument(String documentId){

        UUID documentUUID;
        documentUUID = UUID.fromString(documentId);

        documentService.deleteDocument(documentUUID);

    }
}