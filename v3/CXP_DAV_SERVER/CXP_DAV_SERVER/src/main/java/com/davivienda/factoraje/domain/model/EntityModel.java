package com.davivienda.factoraje.domain.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator; 

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "entities")
public class EntityModel {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    private String code;
    private String name;
    private String nit;
    private String accountBank;
    private String creditLineNumber; 
    private String email;
    private Boolean authenticationMode;
    private Boolean entityType;
    private String district;
    private String municipality;
    private String department;
    private int paymentPolicy;

    @JsonIgnore
    @OneToMany(mappedBy = "entity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserModel> users = new HashSet<>();

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<AgreementModel> agreements = new HashSet<>();

}
