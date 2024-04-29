package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.dto.MoongklWorksInformationDto;
import org.outsourcing.mhadminapi.dto.NotificationDto;
import org.outsourcing.mhadminapi.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID>, PagingAndSortingRepository<Notification, UUID> {

    @Query("SELECT new org.outsourcing.mhadminapi.dto.NotificationDto$GetResponse(" +
            "n.id, n.adminId, n.title, n.content, n.createdAt, n.updatedAt" +
            ") FROM Notification n ORDER BY n.createdAt DESC")
    Page<NotificationDto.GetResponse> findAllByOrderByCreatedAtDesc(Pageable pageable);
}