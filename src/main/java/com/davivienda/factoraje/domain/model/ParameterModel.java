package com.davivienda.factoraje.domain.model;

import java.util.UUID; 

import org.hibernate.annotations.GenericGenerator; 

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue; 
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_parameters")
public class ParameterModel {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "param_key", unique = true, nullable = false)
    private String key;
    @Column(name = "param_value", unique = true, nullable = false)
    private String value;

}
