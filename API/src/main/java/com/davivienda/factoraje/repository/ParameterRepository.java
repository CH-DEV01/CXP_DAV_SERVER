package com.davivienda.factoraje.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.davivienda.factoraje.domain.model.ParameterModel;

@Repository
public interface ParameterRepository extends JpaRepository<ParameterModel, UUID> {

    Optional <ParameterModel> findByKey(String key);

}
