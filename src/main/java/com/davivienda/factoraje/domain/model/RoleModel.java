package com.davivienda.factoraje.domain.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class RoleModel {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "role_id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID roleId;

    @NotBlank(message = "Role name cannot be blank")
    @Column(nullable = false, unique = true)
    private String roleName;

    @NotBlank(message = "Role description cannot be blank")
    @Size(max = 255, message = "Role description cannot exceed 255 characters")
    private String roleDescription;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<PermissionModel> permissions = new HashSet<>();
}