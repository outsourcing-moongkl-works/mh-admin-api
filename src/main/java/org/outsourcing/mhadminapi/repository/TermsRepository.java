package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.entity.Terms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TermsRepository extends JpaRepository<Terms, UUID> {
}
