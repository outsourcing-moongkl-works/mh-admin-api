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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
    private final StringRedisTemplate redisTemplate;
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

    @Transactional
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

    @Transactional
    public void deleteEnterprise(String enterpriseId) {
        Enterprise enterprise = enterpriseRepository.findById(UUID.fromString(enterpriseId))
                .orElseThrow(() -> new EnterpriseException(EnterpriseErrorResult.ENTERPRISE_NOT_FOUND));

        enterpriseRepository.delete(enterprise);
    }

    public AdminDto.PauseEnterpriseResponse pauseEnterprise(AdminDto.PauseEnterpriseRequest request) {
        if (request.getPauseDay() < 0) {
            throw new IllegalArgumentException("Pause day cannot be negative");
        }

        String key = "pause:enterprise:" + request.getEnterpriseId();
        String value = String.valueOf(request.getPauseDay());
        if (request.getPauseDay() > 0) {
            // 일수를 기반으로 만료 시간 계산 (예: 1일 = 24시간)
            long timeout = Duration.ofDays(request.getPauseDay()).getSeconds();
            redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(timeout));
        } else if (request.getPauseDay() == 0) {
            // 무제한 정지
            redisTemplate.opsForValue().set(key, value);
        }

        return AdminDto.PauseEnterpriseResponse.builder()
                .pauseStartAt(LocalDateTime.now())
                .build();
    }

    public AdminDto.PauseUserResponse pauseUser(AdminDto.PauseUserRequest request) {
        if (request.getPauseDay() < 0) {
            throw new IllegalArgumentException("Pause day cannot be negative");
        }

        String key = "pause:user:" + request.getUserId();
        String value = String.valueOf(request.getPauseDay());
        if (request.getPauseDay() > 0) {
            // 일수를 기반으로 만료 시간 계산 (예: 1일 = 24시간)
            long timeout = Duration.ofDays(request.getPauseDay()).getSeconds();
            redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(timeout));
        } else if (request.getPauseDay() == 0) {
            // 무제한 정지
            redisTemplate.opsForValue().set(key, value);
        }

        //@TODO: message send

        return AdminDto.PauseUserResponse.builder()
                .pauseStartAt(LocalDateTime.now())
                .build();
    }

    public AdminDto.UnpauseUserResponse unpauseUser(AdminDto.PauseUserRequest request) {
        String key = "pause:user:" + request.getUserId();
        redisTemplate.delete(key);

        //@TODO: message send
        return AdminDto.UnpauseUserResponse.builder()
                .pauseEndAt(LocalDateTime.now())
                .build();
    }

    public AdminDto.UnpauseEnterpriseResponse unpauseEnterprise(AdminDto.PauseEnterpriseRequest request) {
        String key = "pause:enterprise:" + request.getEnterpriseId();
        redisTemplate.delete(key);

        return AdminDto.UnpauseEnterpriseResponse.builder()
                .pauseEndAt(LocalDateTime.now())
                .build();
    }
}
