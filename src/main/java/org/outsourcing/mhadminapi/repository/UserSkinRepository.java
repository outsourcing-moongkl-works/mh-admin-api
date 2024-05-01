package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.dto.UserDto;
import org.outsourcing.mhadminapi.entity.UserSkin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSkinRepository extends JpaRepository<UserSkin, UUID>{

    //find userhere by userId(ManyToOne)
    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadUserSkinResponse(" +
            "u.id, " +
            "u.email, " +
            "us.skinCloudfrontUrl, " +
            "us.storyCloudfrontUrl, " +
            "us.country, " +
            "us.isPublic, " +
            "us.createdAt " +
            ") " +
            "FROM UserSkin us " +
            "JOIN us.user u " +
            "WHERE u.id = :userId ORDER BY us.createdAt DESC")
    Page<UserDto.ReadUserSkinResponse> findUserSkinByUserId(UUID userId, Pageable pageable);

    //findUserSkinByCountryContaining
//    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadUserSkinResponse(" +
//            "us.user.id, " +
//            "us.user.email, " +
//            "us.skinCloudfrontUrl, " +
//            "us.storyCloudfrontUrl, " +
//            "us.country, " +
//            "us.createdAt, " +
//            "us.isPublic " +
//            ") " +
//            "FROM UserSkin us " +
//            "WHERE us.country LIKE %:country% AND us.createdAt BETWEEN :startDate AND :endDate")
    //use join
    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadUserSkinResponse(" +
            "us.user.id, " +
            "us.user.email, " +
            "us.skinCloudfrontUrl, " +
            "us.storyCloudfrontUrl, " +
            "us.country, " +
            "us.createdAt, " +
            "us.isPublic " +
            ") " +
            "FROM UserSkin us " +
            "JOIN us.user u " +
            "WHERE us.country LIKE %:country% AND us.createdAt BETWEEN :startDate AND :endDate")
    Page<UserDto.ReadUserSkinResponse> findUserSkinByCountryContaining(LocalDateTime startDate, LocalDateTime endDate, String country, Pageable pageable);

    //findUserSkinsByCreatedAtBetween
    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadUserSkinResponse(" +
            "u.id, " +
            "u.email, " +
            "us.skinCloudfrontUrl, " +
            "us.storyCloudfrontUrl, " +
            "us.country, " +
            "us.createdAt, " +
            "us.isPublic " +
            ") " +
            "FROM UserSkin us " +
            "JOIN us.user u " +
            "WHERE us.createdAt BETWEEN :startDate AND :endDate")
    Page<UserDto.ReadUserSkinResponse> findUserSkinByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("DELETE FROM UserSkin us WHERE us.user.id = :userId")
    void deleteAllByUser(UUID userId);

    Optional<UserSkin> findUserSkinById(UUID id);
}
