package com.davivienda.factoraje.controller;

import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.davivienda.factoraje.auth.RolesAllowed;
import com.davivienda.factoraje.service.ProcessFileService;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/archivos")
public class ProcessFileController {

    private final ProcessFileService processFileService;
    private static final Logger log = LoggerFactory.getLogger(ProcessFileController.class);

    @Autowired
    public ProcessFileController(ProcessFileService processFileService) {
        this.processFileService = processFileService;
        log.info("ProcessFileController initialized");
    }

    @RolesAllowed({"AUTHORIZING_TWO_MODE_AUTH", "AUTHORIZING"})
    @PostMapping("/upload-excel-two")
    public ResponseEntity<?> uploadExcelFileTwo(
            @RequestParam("file") @NotNull MultipartFile file,
            @RequestParam("payerId") @NotNull UUID payerId,
            @RequestParam("userId") @NotNull UUID userId) {

        log.info("POST /api/archivos/upload-excel-two - file={}, payerId={}, userId={}",
                file.getOriginalFilename(), payerId, userId);

        if (file.isEmpty()) {
            log.warn("Empty file upload attempted");
            return ResponseEntity.badRequest().body("Por favor, seleccione un archivo para cargar.");
        }
        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") &&
                        !contentType.equals("application/vnd.ms-excel"))) {
            log.warn("Unsupported file type: {}", contentType);
            return ResponseEntity.badRequest().body("Solo se permiten archivos Excel (.xlsx, .xls)");
        }

        try {
            String mensaje = processFileService.procesarYGuardarExcelDos(file, payerId, userId);
            log.info("File processed successfully: {} rows saved", mensaje);
            return ResponseEntity.ok(mensaje);
        } catch (IllegalArgumentException e) {
            log.warn("Validation error processing file: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            log.error("I/O error processing file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error procesando el archivo: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error in upload-excel-two", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + e.getMessage());
        }
    }

    @RolesAllowed({ "MANAGER", "AUTHORIZING" })
    @PostMapping("/upload-excel")
    public ResponseEntity<?> uploadExcelFile(
            @RequestParam("file") @NotNull MultipartFile file,
            @RequestParam("payerId") @NotNull UUID payerId,
            @RequestParam("userId") @NotNull UUID userId) {

        log.info("POST /api/archivos/upload-excel - file={}, payerId={}, userId={}",
                file.getOriginalFilename(), payerId, userId);

        if (file.isEmpty()) {
            log.warn("Empty file upload attempted");
            return ResponseEntity.badRequest().body("Por favor, seleccione un archivo para cargar.");
        }
        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") &&
                        !contentType.equals("application/vnd.ms-excel"))) {
            log.warn("Unsupported file type: {}", contentType);
            return ResponseEntity.badRequest().body("Solo se permiten archivos Excel (.xlsx, .xls)");
        }

        try {
            String mensaje = processFileService.procesarYGuardarExcel(file, payerId, userId);
            log.info("File processed successfully: {} rows saved", mensaje);
            return ResponseEntity.ok(mensaje);
        } catch (IllegalArgumentException e) {
            log.warn("Validation error processing file: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            log.error("I/O error processing file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error procesando el archivo: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error in upload-excel", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + e.getMessage());
        }
    }
}