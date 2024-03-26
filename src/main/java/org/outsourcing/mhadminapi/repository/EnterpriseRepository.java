package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.entity.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface EnterpriseRepository extends JpaRepository<Enterprise, UUID>{

    Optional<Enterprise> findByManagerEmail(String email);

    @Query("SELECT e FROM Enterprise e WHERE e.loginId = :loginId AND e.password = :encode")
    Optional<Enterprise> findByEmailAndPassword(String loginId, String encode);

    Optional<Enterprise> findById(UUID id);

    Optional<Enterprise> findByLoginId(String LoginId);

}
