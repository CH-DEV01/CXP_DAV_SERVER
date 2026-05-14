package com.davivienda.factoraje.domain.model;

import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import lombok.Data;

@Data
@Entity
@Table(name = "term_versions")
public class TermVersionModel {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_type_id", nullable = false)
    private TermTypeModel termType;

    @Column(name = "legal_text", nullable = false, columnDefinition = "TEXT")
    private String legalText;

    @Column(name = "version_number", nullable = false)
    private String versionNumber;

    @Column(name = "publication_date", nullable = false)
    private Instant publicationDate = Instant.now();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;
}