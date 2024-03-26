package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.dto.AdminDto;
import org.outsourcing.mhadminapi.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface AdminRepository extends JpaRepository<Admin, UUID> {
    boolean existsByEmail(String Email);

    Optional<Admin> findByEmail(String Email);

    Optional<Admin> findByEmailAndPassword(String email, String adminPassword);
}
