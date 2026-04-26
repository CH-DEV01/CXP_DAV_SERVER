package com.davivienda.factoraje.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davivienda.factoraje.domain.dto.disbursement.DisbursementDTORequest;
import com.davivienda.factoraje.domain.dto.disbursement.DisbursementDTOResponse;
import com.davivienda.factoraje.service.DisbursementService;

@RestController
@RequestMapping("/api/disbursement/dates")
public class DisbursementController {
    
    private final DisbursementService disbursementService;

    public DisbursementController(DisbursementService disbursementService){
        this.disbursementService = disbursementService;
    } 

    @PostMapping("/calculate")
    public DisbursementDTOResponse calculateDisbursementDate(@RequestBody DisbursementDTORequest request){

        return disbursementService.calculateDisbursementDate(request);

    }

}
