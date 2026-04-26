package com.davivienda.factoraje.utils;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.davivienda.factoraje.repository.PendingSessionRepository;

@Component
public class SessionCleanUpTask {
    
    private final PendingSessionRepository pendingSessionRepository;

    public SessionCleanUpTask(PendingSessionRepository pendingSessionRepository){
        this.pendingSessionRepository = pendingSessionRepository;
    }

    @Scheduled(cron = "0 * * * * *") 
    @Transactional
    public void cleanUpExpiredSessions() {
        pendingSessionRepository.deleteExpiredSessions(LocalDateTime.now());
    }
}
