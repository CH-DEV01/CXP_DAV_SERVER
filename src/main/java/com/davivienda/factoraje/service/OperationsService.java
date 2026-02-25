package com.davivienda.factoraje.service;
import com.davivienda.factoraje.domain.dto.calculate.CalculateDTORequest;
import com.davivienda.factoraje.domain.dto.calculate.CalculateDTOResponse;
import com.davivienda.factoraje.domain.dto.calculate.DetailDTOResponse;
import com.davivienda.factoraje.domain.dto.calculate.PercentageDTORequest;
import com.davivienda.factoraje.domain.dto.calculate.PercentageDTOResponse;
import com.davivienda.factoraje.domain.model.DocumentModel;
import com.davivienda.factoraje.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

@Service
public class OperationsService {

    private final ParameterService parameterService;
    private final DocumentService documentService; 
    private static final Logger log = LoggerFactory.getLogger(OperationsService.class);

    private static final String PARAM_KEY_INTEREST  = "param.key.interest";
    private static final String PARAM_KEY_COMISSION = "param.key.comission";
    private static final String PARAM_KEY_BASE      = "param.key.base";

    private static final BigDecimal DEFAULT_INTEREST_RATE   = new BigDecimal("0.18");
    private static final BigDecimal DEFAULT_IVA_RATE   = new BigDecimal("1.13");
    private static final BigDecimal DEFAULT_COMMISSION_RATE = new BigDecimal("0.0025");
    private static final BigDecimal DEFAULT_BASE            = new BigDecimal("360");

    private static final int SCALE = 2;

    public OperationsService(DocumentService documentService, ParameterService parameterService) {
        this.parameterService = parameterService;
        this.documentService = documentService;
        log.info("OperationsService inicializado y listo para usar el servicio de parámetros en caché.");
    }

    @Transactional
    public CalculateDTOResponse calculate(List<CalculateDTORequest> request) {

        if (request == null || request.isEmpty()) {
            log.warn("La lista de solicitud de cálculo es nula o está vacía. Se retorna una respuesta vacía.");
            throw new IllegalArgumentException("Calculate request no puede ser vacía");
        }

        log.info("Iniciando cálculo para {} item(s).", request.size());

        BigDecimal interestRate   = getParameterAsBigDecimal(PARAM_KEY_INTEREST, DEFAULT_INTEREST_RATE);
        BigDecimal commissionRate = getParameterAsBigDecimal(PARAM_KEY_COMISSION, DEFAULT_COMMISSION_RATE);
        BigDecimal base           = getParameterAsBigDecimal(PARAM_KEY_BASE, DEFAULT_BASE);

        log.info("Parámetros para esta transacción -> Interés={}, Comisión={}, Base={}, Days={}", interestRate, commissionRate, base);

        BigDecimal totalAmountToFinance  = BigDecimal.ZERO;
        BigDecimal totalAmount           = BigDecimal.ZERO;
        BigDecimal totalInterests        = BigDecimal.ZERO;
        BigDecimal totalCommissions      = BigDecimal.ZERO;
        BigDecimal totalAmountToDisburse = BigDecimal.ZERO;

        CalculateDTOResponse response = new CalculateDTOResponse();

        for (CalculateDTORequest dto : request) {

            DetailDTOResponse detail = new DetailDTOResponse();

            DocumentModel documentToModify = new DocumentModel();

            documentToModify = documentService.getDocumentById(UUID.fromString(dto.getDocumentID()))
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el documento con ID: " + dto.getDocumentID()));

            //documentToModify = documentService.getDocumentById(UUID.fromString(dto.getDocumentID())).get();
            
            BigDecimal percentage = getPercentage(new PercentageDTORequest(interestRate, base, dto.getDiffDays()))
                                        .getPercentage();

            System.out.println(dto.getDiffDays());

            BigDecimal amount = dto.getAmount();
            BigDecimal amountToFinance = amount.multiply(percentage);

            // Vamos a refactorizar el calculo de intereses para usar el monto neto de la factura en lugar del monto a financiar
            BigDecimal dailyRate = interestRate.divide(base, 10, RoundingMode.HALF_UP);
            BigDecimal interest = dailyRate.multiply(amount)
                                           .multiply(BigDecimal.valueOf(dto.getDiffDays()));

            // Vamos a dejar de multiplicar por 1.13 porque se necesita que sea más IVA                               
            BigDecimal commission = amount.multiply(commissionRate).multiply(DEFAULT_IVA_RATE);
            BigDecimal disburse = amount.subtract(interest).subtract(commission);

            detail.setDocumentNumber(dto.getDocumentNumber());
            detail.setCutOffDate(dto.getCutOffDate());
            detail.setAmountToFinance(amountToFinance);
            detail.setInterests(interest);
            detail.setCommissions(commission);
            detail.setAmountToBeDisbursed(disburse);
            detail.setFinancingDays(dto.getDiffDays());
            detail.setIssueDate(dto.getIssueDate());
            detail.setAmount(amount);
            response.getDetail().add(detail);

            // Asignar comision y monto a financiar al documento
            documentToModify.setAmountToFinance(amountToFinance);
            documentToModify.setCommission(commission);

            totalAmountToFinance  = totalAmountToFinance.add(amountToFinance);
            totalAmount           = totalAmount.add(amount);
            totalInterests        = totalInterests.add(interest);
            totalCommissions      = totalCommissions.add(commission);
            totalAmountToDisburse = totalAmountToDisburse.add(disburse);
        }

        response.setAmountToFinance(totalAmountToFinance.setScale(SCALE, RoundingMode.HALF_UP));
        response.setInterests(totalInterests.setScale(SCALE, RoundingMode.HALF_UP));
        response.setCommissions(totalCommissions);
        response.setAmountToBeDisbursed(totalAmountToDisburse.setScale(SCALE, RoundingMode.HALF_UP));
        response.setAmount(totalAmount.setScale(SCALE, RoundingMode.HALF_UP));

        log.info("Cálculo finalizado. Total a desembolsar: {}", totalAmountToDisburse);
        log.info("Cálculo finalizado. Comisión total: {}", totalCommissions);
        log.info("Monto total a financiar: {}", totalAmountToFinance);
        log.info("Monto total: {}", totalAmount);
        return response;    
    }

    private BigDecimal getParameterAsBigDecimal(String key, BigDecimal defaultValue) {
        try {
            String value = parameterService.getValueByKey(key); 
            return new BigDecimal(value);
        } catch (ResourceNotFoundException e) {
            log.warn("No se pudo obtener o convertir el parámetro '{}'. Usando valor por defecto: {}. Causa: {}",
                     key, defaultValue, e.getMessage());
            return defaultValue;
        }
    }

    public PercentageDTOResponse getPercentage(PercentageDTORequest req) {

        if (req == null) {
            throw new IllegalArgumentException("El objeto PercentageDTORequest no puede ser nulo.");
        }

        if (req.getBase().compareTo(BigDecimal.ZERO) == 0) {
            log.error("División por cero evitada: la base no puede ser cero.");
            throw new IllegalArgumentException("La base para el cálculo no puede ser cero.");
        }

        // if (req.getBase().compareTo(BigDecimal.ZERO) == 0) {
        //     log.error("División por cero evitada: la base no puede ser cero.");
        //     return new PercentageDTOResponse(BigDecimal.ZERO);
        // }

        BigDecimal dailyRate = req.getInterestRate().divide(req.getBase(), 10, RoundingMode.HALF_UP);
        BigDecimal daysFactor = dailyRate.multiply(BigDecimal.valueOf(req.getDays()));
        BigDecimal divisor = BigDecimal.ONE.add(daysFactor);

        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            return new PercentageDTOResponse(BigDecimal.ZERO);
        }
        BigDecimal percentage = BigDecimal.ONE.divide(divisor, 10, RoundingMode.HALF_UP);
        return new PercentageDTOResponse(percentage);
    }
}
