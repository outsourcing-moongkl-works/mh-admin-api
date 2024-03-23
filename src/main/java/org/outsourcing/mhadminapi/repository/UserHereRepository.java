package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.dto.UserDto;
import org.outsourcing.mhadminapi.dto.UserHereDto;
import org.outsourcing.mhadminapi.entity.UserHere;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.UUID;

public interface UserHereRepository extends JpaRepository<UserHere, UUID>, PagingAndSortingRepository<UserHere, UUID>{

    //find userhere by userId(ManyToOne)
    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadHereResponse(" +
            "uh.id, " +
            "uh.user.email, " +
            "uh.skinCloudfrontUrl, " +
            "uh.storyCloudfrontUrl, " +
            "uh.country " +
            ") " +
            "FROM UserHere uh " +
            "WHERE uh.user.id = :userId")
    Page<UserDto.ReadHereResponse> findUserHereByUserId(UUID userId, Pageable pageable);

    //findUserHereByCountryContaining
    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadHereResponse(" +
            "uh.id, " +
            "uh.user.email, " +
            "uh.skinCloudfrontUrl, " +
            "uh.storyCloudfrontUrl, " +
            "uh.country " +
            ") " +
            "FROM UserHere uh " +
            "WHERE uh.country LIKE %:country%")
    Page<UserDto.ReadHereResponse> findUserHereByCountryContaining(String country, Pageable pageable);

    //findAllUserHere
    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadHereResponse(" +
            "uh.id, " +
            "uh.user.email, " +
            "uh.skinCloudfrontUrl, " +
            "uh.storyCloudfrontUrl, " +
            "uh.country " +
            ") " +
            "FROM UserHere uh")
    Page<UserDto.ReadHereResponse> findAllHere(Pageable pageable);

    //findUserHeresByCreatedAtBetween
    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadHereResponse(" +
            "uh.id, " +
            "uh.user.email, " +
            "uh.skinCloudfrontUrl, " +
            "uh.storyCloudfrontUrl, " +
            "uh.country " +
            ") " +
            "FROM UserHere uh " +
            "WHERE uh.createdAt BETWEEN :startDate AND :endDate")
    Page<UserDto.ReadHereResponse> findHereByCreatedAtBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    //findHereById
    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadHereResponse(" +
            "uh.id, " +
            "uh.user.email, " +
            "uh.skinCloudfrontUrl, " +
            "uh.storyCloudfrontUrl, " +
            "uh.country " +
            ") " +
            "FROM UserHere uh " +
            "WHERE uh.id = :userHereId")
    UserDto.ReadHereResponse findHereById(UUID userHereId);

}
