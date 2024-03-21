package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.dto.MoongklWorksInformationDto;
import org.outsourcing.mhadminapi.dto.NotificationDto;
import org.outsourcing.mhadminapi.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID>, PagingAndSortingRepository<Notification, UUID> {

    Page<NotificationDto.GetResponse> findAllByOrderByCreatedAtDesc(Pageable pageable);
}