package com.davivienda.factoraje.repository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.davivienda.factoraje.domain.model.TermVersionModel;

@Repository
public interface TermVersionRepository extends JpaRepository<TermVersionModel, UUID> {

    List<TermVersionModel> findAllByIsActive(boolean isActive);
}
