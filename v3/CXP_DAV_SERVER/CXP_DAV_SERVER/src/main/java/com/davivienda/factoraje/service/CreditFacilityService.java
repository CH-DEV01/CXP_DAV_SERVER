package com.davivienda.factoraje.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.davivienda.factoraje.domain.dto.creditFacility.CreditFacilityDTORequest;
import com.davivienda.factoraje.domain.dto.creditFacility.CreditFacilityDTOResponse;
import com.davivienda.factoraje.domain.dto.creditFacility.CreditFacilityInternalDTOResponse;
import com.davivienda.factoraje.domain.dto.creditFacility.request.CreditFacilitySearchRequest;
import com.davivienda.factoraje.domain.dto.creditFacility.request.CustomerReference;
import com.davivienda.factoraje.domain.dto.creditFacility.request.InternalDTORequest;

@Service
public class CreditFacilityService {

    private final RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(CreditFacilityService.class);

    public CreditFacilityService(RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
        log.info("Credit Facility service initialized");

    }

    public CreditFacilityInternalDTOResponse getCreditFacilitiesDetails(InternalDTORequest request) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        CreditFacilityDTORequest creditFacilityDTORequest = new CreditFacilityDTORequest();
        CreditFacilitySearchRequest creditFacilitySearchRequest = new CreditFacilitySearchRequest();
        CustomerReference customerReference = new CustomerReference();

        customerReference.setPartyReferenceType(request.getIdentifierType());
        customerReference.setPartyReferenceValue(request.getIdentifier());

        creditFacilitySearchRequest.setCustomerReference(customerReference);

        creditFacilityDTORequest.setCreditFacilitySearchRequest(creditFacilitySearchRequest);

        HttpEntity<CreditFacilityDTORequest> requestEntity = new HttpEntity<>(creditFacilityDTORequest, headers);

        try {

            // ResponseEntity<CreditFacilityDTOResponse> response = restTemplate.exchange(
            //     "http://sv7036lap:8083/credit-facility/v1/search",
            //     HttpMethod.POST,
            //     requestEntity,
            //     CreditFacilityDTOResponse.class);


            CreditFacilityInternalDTOResponse internalResponse = new CreditFacilityInternalDTOResponse();
            internalResponse.setCreditLineNumber(request.getIdentifier());
            internalResponse.setAvailablePercentage("45");

            return internalResponse;
            

        } catch (Exception e) {
            log.error("Error al comunicarse con el API de Credit Facility: {}", e.getMessage(), e);
            
            throw new RuntimeException("No se pudo obtener el detalle de la facilidad de crédito", e);

        }

    }

}
