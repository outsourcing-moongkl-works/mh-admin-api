package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.dto.AdminDto;
import org.outsourcing.mhadminapi.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminRepository extends JpaRepository<Admin, UUID> {
    boolean existsByAdminId(String adminId);
    boolean existsByAdminIdAndPassword(String adminId, String password);

    Optional<Admin> findByAdminId(String adminId);
}
