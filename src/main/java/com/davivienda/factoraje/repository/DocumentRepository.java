package com.davivienda.factoraje.repository;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.davivienda.factoraje.domain.model.DocumentModel;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentModel, UUID> {
    Optional<DocumentModel> findByDocumentNumber(String documentNumber);
}
