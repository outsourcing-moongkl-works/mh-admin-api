package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.dto.UserDto;
import org.outsourcing.mhadminapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    //유저 성별로 조회, 페이징 처리
    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadResponse(" +
            "u.id," +
            "u.email," +
            "u.password," +
            "u.gender," +
            "u.country," +
            "u.phoneNumber" +
            ") FROM User u WHERE u.gender = :gender ORDER BY u.createdAt DESC")
    Page<UserDto.ReadResponse> findUserByGender(@Param("gender") String gender, Pageable pageable);

    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadResponse(" +
            "u.id, u.email, u.password, u.gender, u.country, u.phoneNumber) " +
            "FROM User u WHERE u.email LIKE %:email% ORDER BY u.createdAt DESC")
    Page<UserDto.ReadResponse> findUserByEmailContaining(@Param("email") String email, Pageable pageable);

    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadResponse(" +
            "u.id, u.email, u.password, u.gender, u.country, u.phoneNumber) " +
            "FROM User u WHERE u.country LIKE %:country% ORDER BY u.createdAt DESC")
    Page<UserDto.ReadResponse> findUserByCountryContaining(@Param("country") String country, Pageable pageable);

    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadResponse(" +
            "u.id, u.email, u.password, u.gender, u.country, u.phoneNumber) " +
            "FROM User u WHERE u.phoneNumber LIKE %:phoneNumber% ORDER BY u.createdAt DESC")
    Page<UserDto.ReadResponse> findUserByPhoneNumberContaining(@Param("phoneNumber") String phoneNumber, Pageable pageable);

    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadResponse(" +
            "u.id, u.email, u.password, u.gender, u.country, u.phoneNumber) " +
            "FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate ORDER BY u.createdAt DESC")
    Page<UserDto.ReadResponse> findUserByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadResponse(" +
            "u.id, u.email, u.password, u.gender, u.country, u.phoneNumber) " +
            "FROM User u WHERE u.id = :userId")
    Optional<UserDto.ReadResponse> findUserById(@Param("userId") UUID userId);

    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadResponse(" +
            "u.id, u.email, u.password, u.gender, u.country, u.phoneNumber)" +
            "FROM User u ORDER BY u.createdAt DESC")
    Page<UserDto.ReadResponse> findAllUser(Pageable pageable);
}
