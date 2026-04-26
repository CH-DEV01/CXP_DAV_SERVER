package com.davivienda.factoraje.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.davivienda.factoraje.domain.dto.paymentPolicy.PaymentPolicyRequestDTO;
import com.davivienda.factoraje.domain.dto.paymentPolicy.PaymentPolicyResponseDTO;
import com.davivienda.factoraje.service.PaymentPolicyService;

@RestController
@RequestMapping("/api/policies")
public class PaymentPolicyController {

    private final PaymentPolicyService paymentPolicyService;

    public PaymentPolicyController(PaymentPolicyService paymentPolicyService){
        this.paymentPolicyService = paymentPolicyService;
    }
    
    @PostMapping("/validate-document")
    public PaymentPolicyResponseDTO validateDocument(PaymentPolicyRequestDTO request){
        
        return paymentPolicyService.validateDocumentPolicy(request);
    }
    
}
