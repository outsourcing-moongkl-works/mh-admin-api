package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.dto.UserDto;
import org.outsourcing.mhadminapi.dto.UserSkinDto;
import org.outsourcing.mhadminapi.entity.User;
import org.outsourcing.mhadminapi.entity.UserSkin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.UUID;

public interface UserSkinRepository extends JpaRepository<UserSkin, UUID>{

    //find userhere by userId(ManyToOne)
    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadUserSkinResponse(" +
            "uh.id, " +
            "uh.user.email, " +
            "uh.skinCloudfrontUrl, " +
            "uh.storyCloudfrontUrl, " +
            "uh.country " +
            ") " +
            "FROM UserSkin uh " +
            "WHERE uh.user.id = :userId")
    Page<UserDto.ReadUserSkinResponse> findUserSkinByUserId(UUID userId, Pageable pageable);

    //findUserSkinByCountryContaining
    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadUserSkinResponse(" +
            "uh.id, " +
            "uh.user.email, " +
            "uh.skinCloudfrontUrl, " +
            "uh.storyCloudfrontUrl, " +
            "uh.country " +
            ") " +
            "FROM UserSkin uh " +
            "WHERE uh.country LIKE %:country%")
    Page<UserDto.ReadUserSkinResponse> findUserSkinByCountryContaining(String country, Pageable pageable);

    //findAllUserSkin
    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadUserSkinResponse(" +
            "uh.id, " +
            "uh.user.email, " +
            "uh.skinCloudfrontUrl, " +
            "uh.storyCloudfrontUrl, " +
            "uh.country " +
            ") " +
            "FROM UserSkin uh")
    Page<UserDto.ReadUserSkinResponse> findAllUserSkin(Pageable pageable);

    //findUserSkinsByCreatedAtBetween
    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadUserSkinResponse(" +
            "uh.id, " +
            "uh.user.email, " +
            "uh.skinCloudfrontUrl, " +
            "uh.storyCloudfrontUrl, " +
            "uh.country " +
            ") " +
            "FROM UserSkin uh " +
            "WHERE uh.createdAt BETWEEN :startDate AND :endDate")
    Page<UserDto.ReadUserSkinResponse> findUserSkinByCreatedAtBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query("DELETE FROM UserSkin uh WHERE uh.user.id = :userId")
    void deleteAllByUser(UUID userId);
}
