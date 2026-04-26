package com.davivienda.factoraje.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import org.springframework.stereotype.Service;
import com.davivienda.factoraje.domain.dto.disbursement.DisbursementDTORequest;
import com.davivienda.factoraje.domain.dto.disbursement.DisbursementDTOResponse;

@Service
public class DisbursementService {
    
    private final EntityService entityService;

    public DisbursementService(EntityService entityService){
        this.entityService = entityService;
    }

    public DisbursementDTOResponse calculateDisbursementDate(DisbursementDTORequest request){

        DisbursementDTOResponse response = new DisbursementDTOResponse();
        Boolean entityType = entityService.getEntityById(request.getEntityId()).getEntityType();
        LocalDate nextDay;

        if (entityType) {

            LocalDate nextFriday = request.getToday().with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
            response.setDisbursementDate(nextFriday);
        }

        if(request.getToday().getDayOfWeek() == DayOfWeek.FRIDAY){
            nextDay = request.getToday().plusDays(3);
            response.setDisbursementDate(nextDay);
        } else{

            nextDay = request.getToday().plusDays(1);
            response.setDisbursementDate(nextDay);

        }

        return response;
    }

}
