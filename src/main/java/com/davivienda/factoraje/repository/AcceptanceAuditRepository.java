package com.davivienda.factoraje.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.davivienda.factoraje.domain.model.AcceptanceAuditModel;

@Repository
public interface AcceptanceAuditRepository extends JpaRepository<AcceptanceAuditModel, UUID> {
    
    List<AcceptanceAuditModel> findByUserIdOrderByAcceptanceTimestampDesc(UUID userId);
    
    boolean existsByUserIdAndVersionId(UUID userId, UUID versionId);
}