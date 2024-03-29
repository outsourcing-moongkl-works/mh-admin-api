package org.outsourcing.mhadminapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.auth.JwtTokenProvider;
import org.outsourcing.mhadminapi.dto.AdminDto;
import org.outsourcing.mhadminapi.dto.EnterpriseDto;
import org.outsourcing.mhadminapi.dto.JwtDto;
import org.outsourcing.mhadminapi.entity.Admin;
import org.outsourcing.mhadminapi.entity.Enterprise;
import org.outsourcing.mhadminapi.exception.AdminErrorResult;
import org.outsourcing.mhadminapi.exception.AdminException;
import org.outsourcing.mhadminapi.exception.EnterpriseErrorResult;
import org.outsourcing.mhadminapi.exception.EnterpriseException;
import org.outsourcing.mhadminapi.repository.AdminRepository;
import org.outsourcing.mhadminapi.repository.EnterpriseRepository;
import org.outsourcing.mhadminapi.vo.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService{

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EnterpriseRepository enterpriseRepository;

    @Transactional
    public AdminDto.CreateAdminResponse createAdmin(AdminDto.CreateAdminRequest request) {
        if(adminRepository.existsByEmail(request.getEmail())){
            throw new AdminException(AdminErrorResult.ALREADY_EXIST_ADMIN);
        }

        Admin admin = Admin.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole()))
                .build();

        adminRepository.save(admin);

        log.info("admin created: {}", admin.getId());

        return AdminDto.CreateAdminResponse.builder()
                .adminId(admin.getId().toString())
                .role(admin.getRole().name())
                .build();
    }

    @Transactional
    public AdminDto.LoginAdminResponse login(AdminDto.LoginAdminRequest request) {

        // 이메일을 기반으로 사용자 정보 조회
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AdminException(AdminErrorResult.NOT_FOUND_ADMIN));

        // matches 메서드를 사용하여 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new AdminException(AdminErrorResult.NOT_FOUND_ADMIN);
        }
        JwtDto.JwtRequestDto jwtRequestDto = JwtDto.JwtRequestDto.builder()
                .id(String.valueOf(admin.getId()))
                .email(request.getEmail())
                .role(admin.getRole().name()) // enum to string
                .build();

        String accessToken = jwtTokenProvider.createAccessToken(jwtRequestDto);

        return AdminDto.LoginAdminResponse.builder()
                .adminId(admin.getId().toString())
                .role(admin.getRole().name())
                .accessToken(accessToken)
                .build();
    }

    @Transactional
    public AdminDto.DeleteAdminResponse deleteAdmin(String adminId) {
        Optional<Admin> admin = adminRepository.findById(UUID.fromString(adminId));

        if (admin.isEmpty()) {
            throw new AdminException(AdminErrorResult.NOT_FOUND_ADMIN);
        }

        adminRepository.delete(admin.get());

        return AdminDto.DeleteAdminResponse.builder()
                .adminId(admin.get().getId().toString())
                .role(admin.get().getRole().name())
                .deletedAt(LocalDateTime.now())
                .build();
    }

    public void approveEnterprise(String enterpriseId) {
        Enterprise enterprise = enterpriseRepository.findById(UUID.fromString(enterpriseId))
                .orElseThrow(() -> new EnterpriseException(EnterpriseErrorResult.ENTERPRISE_NOT_FOUND));

        enterprise.updateIsApproved(true);

        enterpriseRepository.save(enterprise);
    }

    public Page<EnterpriseDto.GetEnterprisePageResponse> searchEnterprises(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            String country,
            String enterpriseName,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if(enterpriseName != null && country != null){
            return enterpriseRepository.findByCreatedAtBetweenAndCountryAndEnterpriseNameContaining(startDateTime, endDateTime, country, enterpriseName, pageable);
        } else if(enterpriseName != null){
            return enterpriseRepository.findByCreatedAtBetweenAndEnterpriseNameContaining(startDateTime, endDateTime, enterpriseName, pageable);
        } else if(country != null){
            return enterpriseRepository.findByCreatedAtBetweenAndCountry(startDateTime, endDateTime, country, pageable);
        } else {
            return enterpriseRepository.findByCreatedAtBetween(startDateTime, endDateTime, pageable);
        }
    }

    public void deleteEnterprise(String enterpriseId) {
        Enterprise enterprise = enterpriseRepository.findById(UUID.fromString(enterpriseId))
                .orElseThrow(() -> new EnterpriseException(EnterpriseErrorResult.ENTERPRISE_NOT_FOUND));

        enterpriseRepository.delete(enterprise);
    }
}
