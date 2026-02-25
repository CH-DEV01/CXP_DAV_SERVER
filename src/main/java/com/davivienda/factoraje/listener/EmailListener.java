package com.davivienda.factoraje.listener;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.davivienda.factoraje.event.EmailEvent;
import com.davivienda.factoraje.service.MailjetEmailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor                
public class EmailListener {

    private final MailjetEmailService mailjetEmailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(EmailEvent e) {
        try {
            mailjetEmailService.loadParameters();
            mailjetEmailService.sendEmail(
                    e.getRequest().getDestinatarios(), 
                    e.getRequest().getTipoHtml(),        
                    e.getRequest().getHtmlVariables());   
        } catch (Exception ex) {
            System.err.println("Error");
        }
    }
}
