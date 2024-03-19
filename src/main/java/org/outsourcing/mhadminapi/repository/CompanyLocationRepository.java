package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.entity.CompanyLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompanyLocationRepository extends JpaRepository<CompanyLocation, UUID> {
}
