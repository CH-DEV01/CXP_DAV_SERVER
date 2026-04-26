package com.davivienda.factoraje.domain.model;

import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "permissions")
public class PermissionModel {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID permissionId;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Permission name cannot be blank")
    private String permissionName;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "Permission description cannot be blank, must be less than 255 characters")
    private String permissionDescription;

}