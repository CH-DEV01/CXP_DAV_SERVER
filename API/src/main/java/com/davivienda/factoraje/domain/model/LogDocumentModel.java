package com.davivienda.factoraje.domain.model;

import java.time.Instant;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import lombok.Data;

@Data
@Entity
@Table(name = "document_logs")
public class LogDocumentModel {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "document_id", nullable = false)
    private UUID document_id;

    @Column(name = "user_id", nullable = false)
    private UUID user_id;

    @Column(name = "accion", nullable = false)
    private String action;

    @Column(name = "fecha_hora_accion", nullable = false)
    private Instant date_time_action;

    public LogDocumentModel(UUID documentId, UUID userId, String action) {
        this.document_id = documentId;
        this.user_id = userId;
        this.action = action;
        this.date_time_action = Instant.now();
    }
    
}
