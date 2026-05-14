package com.davivienda.factoraje.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davivienda.factoraje.domain.dto.common.ApiResponse;
import com.davivienda.factoraje.domain.dto.creditFacility.CreditFacilityDTORequest;
import com.davivienda.factoraje.domain.dto.creditFacility.CreditFacilityDTOResponse;
import com.davivienda.factoraje.domain.dto.creditFacility.CreditFacilityInternalDTOResponse;
import com.davivienda.factoraje.domain.dto.creditFacility.request.InternalDTORequest;
import com.davivienda.factoraje.service.CreditFacilityService;

@RestController
@RequestMapping("/api/sso/credit-facility")
public class CreditFacilityController {

    private static final Logger log = LoggerFactory.getLogger(SsoController.class);
    private final CreditFacilityService creditFacilityService;

    public CreditFacilityController(CreditFacilityService creditFacilityService) {
        this.creditFacilityService = creditFacilityService;
        log.info("Credit Facility controller initialized");
    }

    @PostMapping("/details")
    public ResponseEntity<ApiResponse<CreditFacilityInternalDTOResponse>> getDetails(
            @RequestBody InternalDTORequest request) {

        try {

            CreditFacilityInternalDTOResponse detalles = creditFacilityService.getCreditFacilitiesDetails(request);

            return ResponseEntity.ok(ApiResponse.success(detalles, "Facilidad de crédito obtenida correctamente."));

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Ocurrió un error al consultar el sistema central: " + e.getMessage()));
        }
    }
    
}
