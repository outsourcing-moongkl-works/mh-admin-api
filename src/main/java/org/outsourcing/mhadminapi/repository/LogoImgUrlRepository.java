package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.entity.LogoImgUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LogoImgUrlRepository extends JpaRepository<LogoImgUrl, UUID> {
}
