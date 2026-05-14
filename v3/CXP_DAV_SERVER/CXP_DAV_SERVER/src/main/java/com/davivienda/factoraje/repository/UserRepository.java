package com.davivienda.factoraje.repository;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.davivienda.factoraje.domain.model.UserModel;

@Repository
public interface UserRepository extends JpaRepository<UserModel, UUID> {
    
    UserModel findByEmail(String email);

    Optional<UserModel> findByDui(String Dui);

}
