package com.davivienda.factoraje.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.davivienda.factoraje.domain.dto.Emails.DestinatarioRequestDTO;
import com.davivienda.factoraje.domain.dto.Emails.EmailRequestDTO;
import com.davivienda.factoraje.domain.dto.Emails.HTMLVariablesDTO;
import com.davivienda.factoraje.domain.model.AgreementModel;
import com.davivienda.factoraje.domain.model.DocumentModel;
import com.davivienda.factoraje.domain.model.EntityModel;
import com.davivienda.factoraje.domain.model.UserModel;
import com.davivienda.factoraje.event.EmailEvent;
import com.davivienda.factoraje.exception.ResourceNotFoundException;

@Service
public class ProcessFileService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private static final Logger log = LoggerFactory.getLogger(AgreementService.class);
    private final DocumentService documentService;
    private final UserService userService;
    private final AgreementService agreementService;
    private final EntityService entityService;
    private final ParameterService parameterService;
    private final AuditLogService auditLogService;

    UserModel operatorUser = new UserModel();

    private static final String[] COLUMNAS_ESPERADAS = {
            "fechaEmision",
            "monto",
            "numeroDocumento",
            "nombreProveedor",
            "nit",
            "correoProveedor",
            "cuentaBancaria",
            "codigo"
    };

    private static final String PARAM_KEY_EMAIL_MANAGER_BANK = "mailjet.email.manager.bank";
    private static final String PARAM_KEY_NAME_MANAGER_BANK = "mailjet.name.manager.bank";

    public ProcessFileService(AuditLogService auditLogService, ParameterService parameterService, DocumentService documentService, UserService userService,
            AgreementService agreementService, EntityService entityService, ApplicationEventPublisher applicationEventPublisher) {
        this.documentService = documentService;
        this.userService = userService;
        this.agreementService = agreementService;
        this.entityService = entityService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.parameterService = parameterService;
        this.auditLogService = auditLogService;
    }

    private Optional<String> getParameter(String key) {
        try {
            String value = parameterService.getValueByKey(key);
            return Optional.ofNullable(value); 
        } catch (RuntimeException e) {
            log.warn("No se pudo obtener el parámetro '{}'.", key, e); 
            return Optional.empty();
        }
    }

    private void validarCabeceras(Row headerRow) {
        if (headerRow == null) {
            throw new IllegalArgumentException("El archivo Excel está vacío o no tiene fila de cabeceras.");
        }

        for (int i = 0; i < COLUMNAS_ESPERADAS.length; i++) {
            Cell cell = headerRow.getCell(i);
            String headerValue = (cell != null) ? cell.getStringCellValue().trim() : "";

            if (!COLUMNAS_ESPERADAS[i].equalsIgnoreCase(headerValue)) {
                throw new IllegalArgumentException(
                        String.format("Columna %d esperada: '%s', encontrada: '%s'",
                                i + 1, COLUMNAS_ESPERADAS[i], headerValue));
            }
        }
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null)
            return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            default:
                return null;
        }
    }

    @Transactional
    public String procesarYGuardarExcel(MultipartFile file, UUID payerId, UUID userId) throws IOException {

        UserModel uploadedBy = userService.getUserById(userId);
        if (uploadedBy == null) {
            throw new ResourceNotFoundException("El usuario con ID " + userId + " no fue encontrado.");
        }

        // Instancias para la lectura del archivo Excel
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        int filasProcesadas = 0;
        int filasDuplicadas = 0;
        BigDecimal totalMontos = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        List<DestinatarioRequestDTO> destinatarios = new ArrayList<>();
        Set<String> correosVistos = new HashSet<>();

        // Validación de cabeceras del archivo Excel
        Row headerRow = sheet.getRow(0);
        validarCabeceras(headerRow);

        // Procesamiento de filas del archivo Excel
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            // Obtener la fila actual
            Row row = sheet.getRow(i);
            if (row == null)
                continue;

            /* ---- correo del proveedor ---- */
            String emailProveedor = getStringCellValue(row.getCell(5));   // col 5 = email
            String nombreProv     = getStringCellValue(row.getCell(3));   // col 3 = nombre

            /* ─── agrega sólo si es válido y no estaba ─── */
            if (emailProveedor != null && !emailProveedor.isEmpty()
                && correosVistos.add(emailProveedor)) {           // add() devuelve false si ya existía
                destinatarios.add(new DestinatarioRequestDTO(emailProveedor, nombreProv));
            }

            System.err.println(destinatarios);

            // Obtener el número de documento y validar si es nulo o vacío o si ya existe
            String documentNumber = getStringCellValue(row.getCell(2));

            Optional<DocumentModel> documentoExistente = documentService.getDocumentByNumber(documentNumber);

            if (documentNumber == null || documentNumber.isEmpty() || documentoExistente.isPresent()) {
                // Si el número es nulo, vacío O si el documento YA EXISTE, salta a la siguiente iteración.
                continue;
            }

            // Creación de instancias
            DocumentModel documento = new DocumentModel(); // Documento que va a representar una factura/ccf/etc
            AgreementModel agreement = new AgreementModel(); // Acuerdo que representa la relación entre el pagador y el
                                                             // proveedor

            EntityModel supplierEntity = new EntityModel(); // Proveedor de la factura/ccf/etc
            EntityModel payerEntity = new EntityModel(); // Pagador de la factura/ccf/etc

            // Obtenemos el codigo del proveedor para validar si existe
            String supplierCode = getStringCellValue(row.getCell(7));

            documento.setDocumentNumber(getStringCellValue(row.getCell(2))); // Número de documento
    
            // Cell cell = row.getCell(1);
            // double montoRaw = cell.getNumericCellValue(); 

            Cell amountCell = row.getCell(1);
            if (amountCell == null || amountCell.getCellType() == CellType.BLANK) {
                throw new IllegalArgumentException("La celda de monto está vacía en la fila " + (i + 1) + ".");
            }
            
            double montoRaw = amountCell.getNumericCellValue(); 

            BigDecimal monto = BigDecimal.valueOf(montoRaw).setScale(2, RoundingMode.HALF_UP);
            documento.setAmount(monto);

            totalMontos = totalMontos.add(monto);
   
            documento.setIssueDate(getStringCellValue(row.getCell(0))); // Fecha de emisión del documento
            documento.setStatus("APPROVED"); // Estado del documento (asumimos que es true por defecto)
            documento.setStatusUpdateDate(getStringCellValue(row.getCell(0))); // Fecha de actualización del estado
                                                                               // (asumimos que es la misma fecha de
                                                                               // emisión)
            documento.setDisbursementDate(getStringCellValue(row.getCell(0))); // Fecha de desembolso (asumimos que es
                                                                               // la misma fecha de emisión)
            documento.setSupplierName(getStringCellValue(row.getCell(3))); // Nombre del proveedor

            // Con el id del pagador recibido, buscamos al pagador

            if (payerId == null) {
                throw new IllegalArgumentException("El id del pagador no puede ser null");
            }

            if (entityService.getEntityById(payerId) == null) {
                throw new ResourceNotFoundException("El pagador con ID " + payerId + " no fue encontrado.");
            } else {
                // Buscamos al pagador por ID y asignamos al acuerdo
                payerEntity = entityService.getEntityById(payerId);
                agreement.setPayer(payerId); // Asignar el pagador al acuerdo
            }

            // Validaciones para el proveedor
            if (supplierCode == null || supplierCode.isEmpty()) {
                throw new IllegalArgumentException("El código del proveedor no puede ser null.");
            }

            if (entityService.findByCode(supplierCode) == null) {

                supplierEntity.setName(getStringCellValue(row.getCell(3)));
                supplierEntity.setEmail(getStringCellValue(row.getCell(5)));
                supplierEntity.setCode(getStringCellValue(row.getCell(7)));
                supplierEntity.setNit(getStringCellValue(row.getCell(4)));
                supplierEntity.setAccountBank(getStringCellValue(row.getCell(6)));
                supplierEntity.setEntityType(false); // Asumimos que es un proveedor

                entityService.createEntity(supplierEntity); // Guardar el proveedor en la base de datos

                agreement.setSupplier(supplierEntity.getId()); // Asignar el proveedor al acuerdo

            } else {

                // Si el proveedor ya existe, lo buscamos por código y lo asignamos al acuerdo
                supplierEntity = entityService.findByCode(supplierCode);

                agreement.setSupplier(supplierEntity.getId());
                // documento.setSupplier(supplier);
            }

            documento.setUploadedBy(uploadedBy);
            //auditLogService.logAction(documento.getDocument_id(), userId, "UPLOADED");

            // Verificar si el acuerdo ya existe
            String agreementIdentifier = payerId.toString().concat(supplierEntity.getId().toString());

            if (agreementService.findByIdentifier(agreementIdentifier) == null) {

                agreement.setIdentifier(agreementIdentifier);
                agreement.setName("Convenio entre " + payerEntity.getName() + " y " + supplierEntity.getName());
                agreement.setPayer(entityService.getEntityById(payerId).getId());
                agreement.setSupplier(supplierEntity.getId());

                operatorUser = userService.getUserById(userId);

                // Agregar el documento al convenio nuevo
                documento.setAgreement(agreement);
                //documento.setUploadedBy(operatorUser);
                //documento.setUploadedBy(uploadedBy);

                // guardar
                agreementService.save(agreement);

            } else {
                agreement = agreementService.findByIdentifier(agreementIdentifier);
                // Agregar el documento al convenio existente
                documento.setAgreement(agreement);

                // guardar
                agreementService.save(agreement);
            }

            documentService.createDocument(documento); // Guardar el documento en la base de datos

            filasProcesadas++;
        }

        workbook.close();

        // Correo al pagador
        // REFACTORIZAR: Modificar para recorrer la lista de usuarios y enviar correo a los usuarios vinculados a la entidad y que tengan rol pagador
        EntityModel payer = new EntityModel();
        payer = entityService.getEntityById(payerId);


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
        payerVariables.setMontoDesembolsar(totalMontos);

        request.setTipoHtml(5);
        request.setDestinatarios(destinos);
        request.setHtmlVariables(payerVariables);

        EmailEvent evt = new EmailEvent(request);
        applicationEventPublisher.publishEvent(evt);

        // Correo a proveedores
        EmailRequestDTO suppliersRequest = new EmailRequestDTO();
        HTMLVariablesDTO supplierVariables = new HTMLVariablesDTO();

        supplierVariables.setNombreEmpresa("");
        supplierVariables.setNombreProveedor("");
        supplierVariables.setNumeroCuentaProveedor("");
        supplierVariables.setNumeroLineaCredito("");
        supplierVariables.setMontoDesembolsar(totalMontos);

        suppliersRequest.setTipoHtml(2);
        suppliersRequest.setDestinatarios(destinatarios);
        suppliersRequest.setHtmlVariables(supplierVariables);

        EmailEvent supplierEvt = new EmailEvent(suppliersRequest);
        applicationEventPublisher.publishEvent(supplierEvt);

        String mensaje = "Archivo procesado. " + filasProcesadas + " registros guardados en la base de datos.";
        if (filasDuplicadas > 0) {
            mensaje += " " + filasDuplicadas + " registros duplicados encontrados y omitidos.";
        }

        return mensaje;
    }
 
    @Transactional
    public String procesarYGuardarExcelDos(MultipartFile file, UUID payerId, UUID userId) throws IOException {

        // Instancias para la lectura del archivo Excel
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        int filasProcesadas = 0;
        int filasDuplicadas = 0;
        BigDecimal totalMontos = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        List<DestinatarioRequestDTO> destinatarios = new ArrayList<>();
        Set<String> correosVistos = new HashSet<>();

        // Validación de cabeceras del archivo Excel
        Row headerRow = sheet.getRow(0);
        validarCabeceras(headerRow);

        // Procesamiento de filas del archivo Excel
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            // Obtener la fila actual
            Row row = sheet.getRow(i);
            if (row == null)
                continue;

            /* ---- correo del proveedor ---- */
            String emailProveedor = getStringCellValue(row.getCell(5));   // col 5 = email
            String nombreProv     = getStringCellValue(row.getCell(3));   // col 3 = nombre

            /* ─── agrega sólo si es válido y no estaba ─── */
            if (emailProveedor != null && !emailProveedor.isEmpty()
                && correosVistos.add(emailProveedor)) {           // add() devuelve false si ya existía
                destinatarios.add(new DestinatarioRequestDTO(emailProveedor, nombreProv));
            }

            // Obtener el número de documento y validar si es nulo o vacío
            String documentNumber = getStringCellValue(row.getCell(2));

            if (documentNumber == null || documentNumber.isEmpty()) {
                continue;
            }

            // Creación de instancias
            DocumentModel documento = new DocumentModel(); // Documento que va a representar una factura/ccf/etc
            AgreementModel agreement = new AgreementModel(); // Acuerdo que representa la relación entre el pagador y el
                                                             // proveedor

            EntityModel supplierEntity = new EntityModel(); // Proveedor de la factura/ccf/etc
            EntityModel payerEntity = new EntityModel(); // Pagador de la factura/ccf/etc

            // Obtenemos el codigo del proveedor para validar si existe
            String supplierCode = getStringCellValue(row.getCell(7));

            documento.setDocumentNumber(getStringCellValue(row.getCell(2))); // Número de documento

            Cell cell = row.getCell(1);
            double montoRaw = cell.getNumericCellValue(); 
            BigDecimal monto = BigDecimal.valueOf(montoRaw).setScale(2, RoundingMode.HALF_UP);
            documento.setAmount(monto);

            totalMontos = totalMontos.add(monto);
            
            documento.setIssueDate(getStringCellValue(row.getCell(0))); // Fecha de emisión del documento
            documento.setStatus("SELECTED"); // Estado del documento (asumimos que es true por defecto)
            documento.setStatusUpdateDate(getStringCellValue(row.getCell(0))); // Fecha de actualización del estado
                                                                               // (asumimos que es la misma fecha de
                                                                               // emisión)
            documento.setDisbursementDate(getStringCellValue(row.getCell(0))); // Fecha de desembolso (asumimos que es
                                                                               // la misma fecha de emisión)
            documento.setSupplierName(getStringCellValue(row.getCell(3))); // Nombre del proveedor

            // Con el id del pagador recibido, buscamos al pagador

            if (payerId == null) {
                throw new IllegalArgumentException("El ID del pagador recibido no puede ser nulo.");
            }

            if (entityService.getEntityById(payerId) == null) {
                throw new ResourceNotFoundException("El pagador con ID " + payerId + " no existe.");
            } else {
                // Buscamos al pagador por ID y asignamos al acuerdo
                payerEntity = entityService.getEntityById(payerId);
                agreement.setPayer(payerId); // Asignar el pagador al acuerdo
            }

            // Validaciones para el proveedor
            if (supplierCode == null || supplierCode.isEmpty()) {
                throw new IllegalArgumentException("El código del proveedor no puede ser nulo o vacío.");
            }

            if (entityService.findByCode(supplierCode) == null) {

                supplierEntity.setName(getStringCellValue(row.getCell(3)));
                supplierEntity.setEmail(getStringCellValue(row.getCell(5)));
                supplierEntity.setCode(getStringCellValue(row.getCell(7)));
                supplierEntity.setNit(getStringCellValue(row.getCell(4)));
                supplierEntity.setAccountBank(getStringCellValue(row.getCell(6)));
                supplierEntity.setEntityType(false); // Asumimos que es un proveedor

                entityService.createEntity(supplierEntity); // Guardar el proveedor en la base de datos

                agreement.setSupplier(supplierEntity.getId()); // Asignar el proveedor al acuerdo

            } else {

                // Si el proveedor ya existe, lo buscamos por código y lo asignamos al acuerdo
                supplierEntity = entityService.findByCode(supplierCode);

                agreement.setSupplier(supplierEntity.getId());
                // documento.setSupplier(supplier);
            }

            // Verificar si el acuerdo ya existe
            String agreementIdentifier = payerId.toString().concat(supplierEntity.getId().toString());

            if (agreementService.findByIdentifier(agreementIdentifier) == null) {

                agreement.setIdentifier(agreementIdentifier);
                agreement.setName("Convenio entre " + payerEntity.getName() + " y " + supplierEntity.getName());
                agreement.setPayer(entityService.getEntityById(payerId).getId());
                agreement.setSupplier(supplierEntity.getId());

                operatorUser = userService.getUserById(userId);

                // Agregar el documento al convenio nuevo
                documento.setAgreement(agreement);
                documento.setUploadedBy(operatorUser);

                // guardar
                agreementService.save(agreement);

            } else {
                agreement = agreementService.findByIdentifier(agreementIdentifier);
                // Agregar el documento al convenio existente
                documento.setAgreement(agreement);

                // guardar
                agreementService.save(agreement);
            }

            documentService.createDocument(documento); // Guardar el documento en la base de datos

            filasProcesadas++;
        }

        workbook.close();

        // Correo al banco
        EntityModel payer = new EntityModel();
        payer = entityService.getEntityById(payerId);
        EmailRequestDTO request = new EmailRequestDTO();
        HTMLVariablesDTO bankVariables = new HTMLVariablesDTO();

        DestinatarioRequestDTO bank = new DestinatarioRequestDTO();
        
        bank.setName(getParameter(PARAM_KEY_NAME_MANAGER_BANK).get());
        bank.setEmail(getParameter(PARAM_KEY_EMAIL_MANAGER_BANK).get());

        List<DestinatarioRequestDTO> destinos = new ArrayList<>();
        destinos.add(bank);

        bankVariables.setNombreEmpresa(payer.getName());
        bankVariables.setNombreProveedor("");
        bankVariables.setNumeroCuentaProveedor("");
        bankVariables.setNumeroLineaCredito(payer.getCreditLineNumber());
        bankVariables.setMontoDesembolsar(totalMontos);
        bankVariables.setNIT(payer.getNit());

        request.setTipoHtml(4);
        request.setDestinatarios(destinos);
        request.setHtmlVariables(bankVariables);

        EmailEvent evt = new EmailEvent(request);
        applicationEventPublisher.publishEvent(evt);

        // Correo a proveedores
        EmailRequestDTO suppliersRequest = new EmailRequestDTO();
        HTMLVariablesDTO supplierVariables = new HTMLVariablesDTO();

        supplierVariables.setNombreEmpresa("");
        supplierVariables.setNombreProveedor("");
        supplierVariables.setNumeroCuentaProveedor("");
        supplierVariables.setNumeroLineaCredito("");
        supplierVariables.setMontoDesembolsar(totalMontos);

        suppliersRequest.setTipoHtml(2);
        suppliersRequest.setDestinatarios(destinatarios);
        suppliersRequest.setHtmlVariables(supplierVariables);

        EmailEvent supplierEvt = new EmailEvent(suppliersRequest);
        applicationEventPublisher.publishEvent(supplierEvt);

        String mensaje = "Archivo procesado. " + filasProcesadas + " registros guardados en la base de datos.";
        if (filasDuplicadas > 0) {
            mensaje += " " + filasDuplicadas + " registros duplicados encontrados y omitidos.";
        }

        return mensaje;
    }
}