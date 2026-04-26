package com.davivienda.factoraje.domain.model;

import java.time.Instant;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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