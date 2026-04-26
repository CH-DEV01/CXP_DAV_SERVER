package com.davivienda.factoraje.domain.model;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
