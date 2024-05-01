package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.dto.EnterpriseDto;
import org.outsourcing.mhadminapi.entity.Enterprise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface EnterpriseRepository extends JpaRepository<Enterprise, UUID>{

    @Query("SELECT e FROM Enterprise e WHERE e.loginId = :loginId AND e.password = :password")
    Optional<Enterprise> findByEmailAndPassword(@Param("loginId") String loginId, @Param("password") String password);

    Optional<Enterprise> findById(UUID id);

    Optional<Enterprise> findByLoginId(String loginId);

    @Query("SELECT new org.outsourcing.mhadminapi.dto.EnterpriseDto$ReadResponse(" +
            "e.id, e.name, e.businessNumber, e.corporateNumber, e.address, e.country, e.managerName, e.managerPhone, e.managerEmail, l.cloudfrontUrl, e.isApproved) " +
            "FROM Enterprise e " +
            "JOIN e.logoImgUrl l " +
            "WHERE e.createdAt BETWEEN :startDate AND :endDate " +
            "AND e.country = :country " +
            "AND e.name LIKE %:enterpriseName% " +
            "ORDER BY e.createdAt DESC")
    Page<EnterpriseDto.ReadResponse> findByCreatedAtBetweenAndCountryAndEnterpriseNameContaining(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("country") String country,
            @Param("enterpriseName") String enterpriseName,
            Pageable pageable);

    @Query("SELECT new org.outsourcing.mhadminapi.dto.EnterpriseDto$ReadResponse(" +
            "e.id, e.name, e.businessNumber, e.corporateNumber, e.address, e.country, e.managerName, e.managerPhone, e.managerEmail, l.cloudfrontUrl, e.isApproved) " +
            "FROM Enterprise e " +
            "JOIN e.logoImgUrl l " +
            "WHERE e.createdAt BETWEEN :startDate AND :endDate " +
            "AND e.name LIKE %:enterpriseName% " +
            "ORDER BY e.createdAt DESC")
    Page<EnterpriseDto.ReadResponse> findByCreatedAtBetweenAndEnterpriseNameContaining(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("enterpriseName") String enterpriseName,
            Pageable pageable);

    @Query("SELECT new org.outsourcing.mhadminapi.dto.EnterpriseDto$ReadResponse(" +
            "e.id, e.name, e.businessNumber, e.corporateNumber, e.address, e.country, e.managerName, e.managerPhone, e.managerEmail, l.cloudfrontUrl, e.isApproved) " +
            "FROM Enterprise e " +
            "JOIN e.logoImgUrl l " +
            "WHERE e.createdAt BETWEEN :startDate AND :endDate " +
            "AND e.country = :country " +
            "ORDER BY e.createdAt DESC")
    Page<EnterpriseDto.ReadResponse> findByCreatedAtBetweenAndCountry(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("country") String country,
            Pageable pageable);

    @Query("SELECT new org.outsourcing.mhadminapi.dto.EnterpriseDto$ReadResponse(" +
            "e.id, e.name, e.businessNumber, e.corporateNumber, e.address, e.country, e.managerName, e.managerPhone, e.managerEmail, l.cloudfrontUrl, e.isApproved) " +
            "FROM Enterprise e " +
            "JOIN e.logoImgUrl l " +
            "WHERE e.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY e.createdAt DESC")
    Page<EnterpriseDto.ReadResponse> findByCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}
