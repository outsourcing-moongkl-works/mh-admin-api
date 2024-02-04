package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StaffRepository extends JpaRepository<Staff, UUID> {
}
