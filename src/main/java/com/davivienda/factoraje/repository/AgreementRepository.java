package com.davivienda.factoraje.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.davivienda.factoraje.domain.model.AgreementModel;

public interface AgreementRepository extends JpaRepository<AgreementModel, UUID> {
    
    public AgreementModel findByIdentifier(String identifier);
    
}
