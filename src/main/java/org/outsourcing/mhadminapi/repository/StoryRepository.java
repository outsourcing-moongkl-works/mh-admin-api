package org.outsourcing.mhadminapi.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.outsourcing.mhadminapi.dto.EnterpriseDto;
import org.outsourcing.mhadminapi.entity.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;
@Repository
public interface StoryRepository extends JpaRepository<Story, UUID> {

    void deleteById(UUID id);

    @Query("SELECT new org.outsourcing.mhadminapi.dto.EnterpriseDto$GetStoryPageResponse(" +
            "s.id, siu.cloudfrontUrl, s.isPublic, s.useCount, s.shareCount, s.viewCount, s.createdAt) " +
            "FROM Story s " +
            "JOIN s.storyImgUrl siu " +
            "WHERE s.enterprise.id = :enterpriseId " +
            "AND s.createdAt BETWEEN :startDateTime AND :endDateTime " +
            "ORDER BY s.createdAt DESC")
    Page<EnterpriseDto.GetStoryPageResponse> findByEnterpriseIdAndCreatedAtBetween(UUID enterpriseId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    @Query("SELECT new org.outsourcing.mhadminapi.dto.EnterpriseDto$GetStoryPageResponse(" +
            "s.id, siu.cloudfrontUrl, s.isPublic, s.useCount, s.shareCount, s.viewCount, s.createdAt) " +
            "FROM Story s " +
            "JOIN s.storyImgUrl siu " +
            "WHERE s.enterprise.id = :enterpriseId " +
            "AND s.createdAt BETWEEN :startDateTime AND :endDateTime " +
            "AND s.isPublic = true " +
            "ORDER BY s.createdAt DESC")
    Page<EnterpriseDto.GetStoryPageResponse> findByEnterpriseIdAndCreatedAtBetweenAndPublic(UUID enterpriseId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    @Query("SELECT new org.outsourcing.mhadminapi.dto.EnterpriseDto$GetStoryPageResponse(" +
            "s.id, siu.cloudfrontUrl, s.isPublic, s.useCount, s.shareCount, s.viewCount, s.createdAt) " +
            "FROM Story s " +
            "JOIN s.storyImgUrl siu " +
            "WHERE s.enterprise.id = :enterpriseId " +
            "AND s.createdAt BETWEEN :startDateTime AND :endDateTime " +
            "AND s.isPublic = false " +
            "ORDER BY s.createdAt DESC")
    Page<EnterpriseDto.GetStoryPageResponse> findByEnterpriseIdAndCreatedAtBetweenAndNotPublic(UUID enterpriseId, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);
}
