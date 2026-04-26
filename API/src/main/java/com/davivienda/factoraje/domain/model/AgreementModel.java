package com.davivienda.factoraje.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "agreements")
public class AgreementModel {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "agreement_id", updatable = false, nullable = false)
    private UUID agreement_id;

    @NotBlank(message = "Agreement name cannot be blank")
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String identifier;

    @Column(nullable = false)
    private UUID payer;

    @Column(nullable = false)
    private UUID supplier;

    @JsonManagedReference
    @OneToMany(mappedBy = "agreement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentModel> documents = new ArrayList<>();
}