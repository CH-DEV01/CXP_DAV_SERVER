package com.davivienda.factoraje.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.davivienda.factoraje.auth.PermissionsAllowed;
import com.davivienda.factoraje.auth.RolesAllowed;
import com.davivienda.factoraje.domain.dto.calculate.CalculateDTORequest;
import com.davivienda.factoraje.domain.dto.calculate.CalculateDTOResponse;
import com.davivienda.factoraje.service.OperationsService;

@RestController
@RequestMapping("api/operations")
public class OperationsController {

    private static final Logger logger = LoggerFactory.getLogger(OperationsController.class);
    private final OperationsService operationsService;

    public OperationsController(OperationsService operationsService) {
        this.operationsService = operationsService;
        logger.info("OperationsController initialized");
    }

    @RolesAllowed({"SUPPLIER", "SUPPLIER_TWO_MODE_AUTH"})
    @PermissionsAllowed({ "select_documents_execute", "select_documents_execute_two_mode_auth" })
    @PostMapping("/calculate")
    public ResponseEntity<?> doOperations(@RequestBody List<CalculateDTORequest> request) {
        logger.info("POST /api/operations/calculate - Received {} calculation items",
                request != null ? request.size() : 0);

        if (request == null || request.isEmpty()) {
            logger.warn("Calculation request is null or empty");
            return ResponseEntity
                    .badRequest()
                    .body("El cuerpo de la petición debe contener al menos un elemento para calcular");
        }
        CalculateDTOResponse response = operationsService.calculate(request);
        logger.info("Calculation successful for {} items", request.size());
        return ResponseEntity.ok(response);
    }

}
