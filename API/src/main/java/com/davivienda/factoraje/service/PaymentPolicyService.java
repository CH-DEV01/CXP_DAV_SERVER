package com.davivienda.factoraje.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import com.davivienda.factoraje.domain.dto.paymentPolicy.PaymentPolicyRequestDTO;
import com.davivienda.factoraje.domain.dto.paymentPolicy.PaymentPolicyResponseDTO;
import com.davivienda.factoraje.domain.model.EntityModel;

@Service
public class PaymentPolicyService {

    private final EntityService entityService;

    public PaymentPolicyService(EntityService entityService){

        this.entityService = entityService;
    }

    public PaymentPolicyResponseDTO validateDocumentPolicy(PaymentPolicyRequestDTO requestDTO){

        PaymentPolicyResponseDTO response = new PaymentPolicyResponseDTO();
        long fechaSerial = Long.parseLong("45927");

        LocalDate epoch = LocalDate.of(1900, 1, 1);
        LocalDate fechaConvertida = epoch.plusDays(fechaSerial - 2);

        //EntityModel entity = entityService.getEntityById(requestDTO.getEntityId());

        // int policy = entity.getPaymentPolicy();
        int policy = 60;

        LocalDate today = LocalDate.now();
        long daysPassed = ChronoUnit.DAYS.between(fechaConvertida, today);

        if(daysPassed < policy){
            response.setDocumentNumber("1");
            response.setIssueDate(fechaConvertida);
            response.setPassedDays(daysPassed);
            response.setTodayDate(today);
            response.setResult(true);
            response.setPolicy(policy);

            return response;
        }

        response.setDocumentNumber("1");
        response.setIssueDate(fechaConvertida);
        response.setPassedDays(daysPassed);
        response.setTodayDate(today);
        response.setResult(false);
        response.setPolicy(policy);

        return response;
    }
    
}
