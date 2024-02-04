package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
