package com.davivienda.factoraje.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.davivienda.factoraje.domain.model.LogDocumentModel;

@Repository
public interface LogDocumentRepository extends JpaRepository<LogDocumentModel, UUID> {

}
