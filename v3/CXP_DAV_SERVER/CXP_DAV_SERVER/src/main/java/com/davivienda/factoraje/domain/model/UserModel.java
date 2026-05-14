package com.davivienda.factoraje.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserModel {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @Column(nullable = false)
    private String dui;

    @Column(nullable = false)
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @OneToMany(mappedBy = "uploadedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentModel> uploadedDocuments = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "selectedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentModel> selectedDocuments = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "approvedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentModel> approvedDocuments = new ArrayList<>();

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id", nullable = true)
    private EntityModel entity;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private RoleModel role;
}