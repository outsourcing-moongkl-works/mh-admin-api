package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.dto.AdminDto;
import org.outsourcing.mhadminapi.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminRepository extends JpaRepository<Admin, UUID> {
    boolean existsByAdminEmail(String adminEmail);

    Optional<Admin> findByAdminEmail(String adminEmail);
}
