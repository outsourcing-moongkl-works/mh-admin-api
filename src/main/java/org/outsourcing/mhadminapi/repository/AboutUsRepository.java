package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.entity.AboutUs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AboutUsRepository extends JpaRepository<AboutUs, UUID> {
}
