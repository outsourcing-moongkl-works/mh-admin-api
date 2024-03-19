package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}
