package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.dto.UserDto;
import org.outsourcing.mhadminapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    //유저 성별로 조회, 페이징 처리
    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadResponse(" +
            "u.id," +
            "u.email," +
            "u.password," +
            "u.gender," +
            "u.country," +
            "u.phoneNumber" +
            ") FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate AND u.gender = :gender ORDER BY u.createdAt DESC")
    Page<UserDto.ReadResponse> findUserByGender(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("gender") String gender, Pageable pageable);

    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadResponse(" +
            "u.id, u.email, u.password, u.gender, u.country, u.phoneNumber) " +
            "FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate AND u.email LIKE %:email% ORDER BY u.createdAt DESC")
    Page<UserDto.ReadResponse> findUserByEmailContaining(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("email") String email, Pageable pageable);

    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadResponse(" +
            "u.id, u.email, u.password, u.gender, u.country, u.phoneNumber) " +
            "FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate AND u.country LIKE %:country% ORDER BY u.createdAt DESC")
    Page<UserDto.ReadResponse> findUserByCountryContaining(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("country") String country, Pageable pageable);

    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadResponse(" +
            "u.id, u.email, u.password, u.gender, u.country, u.phoneNumber) " +
            "FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate AND u.phoneNumber LIKE %:phoneNumber% ORDER BY u.createdAt DESC")
    Page<UserDto.ReadResponse> findUserByPhoneNumberContaining(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("phoneNumber") String phoneNumber, Pageable pageable);

    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadResponse(" +
            "u.id, u.email, u.password, u.gender, u.country, u.phoneNumber) " +
            "FROM User u WHERE u.id = :userId ORDER BY u.createdAt DESC")
    Optional<UserDto.ReadResponse> findUserById(@Param("userId") UUID userId);

    @Query("SELECT new org.outsourcing.mhadminapi.dto.UserDto$ReadResponse(" +
            "u.id, u.email, u.password, u.gender, u.country, u.phoneNumber)" +
            "FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate ORDER BY u.createdAt DESC")
    Page<UserDto.ReadResponse> findAllUser(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
}
