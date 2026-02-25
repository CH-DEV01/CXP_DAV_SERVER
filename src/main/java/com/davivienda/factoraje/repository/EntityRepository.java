package com.davivienda.factoraje.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.davivienda.factoraje.domain.model.EntityModel;

@Repository
public interface EntityRepository extends JpaRepository<EntityModel, UUID>  {
    Optional<EntityModel> findByCode(String code);
}
