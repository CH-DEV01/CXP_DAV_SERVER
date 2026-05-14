package com.davivienda.factoraje.domain.model;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sso_pending_sessions")
public class PendingSessionModel {

    @Id
    private String sessionToken;
    private String userDui;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    
}
