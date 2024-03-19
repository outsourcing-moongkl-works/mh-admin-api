package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.entity.Terms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TermsRepository extends JpaRepository<Terms, UUID> {
}
