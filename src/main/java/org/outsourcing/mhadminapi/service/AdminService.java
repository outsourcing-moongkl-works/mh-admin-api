package org.outsourcing.mhadminapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.auth.JwtTokenProvider;
import org.outsourcing.mhadminapi.dto.*;
import org.outsourcing.mhadminapi.entity.Admin;
import org.outsourcing.mhadminapi.entity.Enterprise;
import org.outsourcing.mhadminapi.entity.User;
import org.outsourcing.mhadminapi.entity.UserSkin;
import org.outsourcing.mhadminapi.exception.AdminErrorResult;
import org.outsourcing.mhadminapi.exception.AdminException;
import org.outsourcing.mhadminapi.exception.EnterpriseErrorResult;
import org.outsourcing.mhadminapi.exception.EnterpriseException;
import org.outsourcing.mhadminapi.repository.AdminRepository;
import org.outsourcing.mhadminapi.repository.EnterpriseRepository;
import org.outsourcing.mhadminapi.repository.UserRepository;
import org.outsourcing.mhadminapi.repository.UserSkinRepository;
import org.outsourcing.mhadminapi.sqs.SqsSender;
import org.outsourcing.mhadminapi.vo.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService{

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EnterpriseRepository enterpriseRepository;
    private final StringRedisTemplate redisTemplate;
    private final SqsSender sqsSender;
    private final UserRepository userRepository;
    private final UserSkinRepository userSkinRepository;
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

        //create enterprise
        Map<String, String> messageMap = new LinkedHashMap<>();
        messageMap.put("id", enterprise.getId().toString());
        messageMap.put("name", enterprise.getName());
        messageMap.put("country", enterprise.getCountry());
        messageMap.put("managerEmail", enterprise.getManagerEmail());

        messageMap.put("logoImgUrlId", enterprise.getLogoImgUrl().getId().toString());
        messageMap.put("logoImgUrlS3Url", enterprise.getLogoImgUrl().getS3Url());
        messageMap.put("logoImgUrlCloudfrontUrl", enterprise.getLogoImgUrl().getCloudfrontUrl());

        MessageDto messageDto = sqsSender.createMessageDtoFromRequest("create enterprise", messageMap);

        sqsSender.sendToSQS(messageDto);
    }

    public Page<EnterpriseDto.ReadResponse> searchEnterprises(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            String country,
            String enterpriseName,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<EnterpriseDto.ReadResponse> response;

        if(enterpriseName != null && country != null){
            response = enterpriseRepository.findByCreatedAtBetweenAndCountryAndEnterpriseNameContaining(startDateTime, endDateTime, country, enterpriseName, pageable);
        } else if(enterpriseName != null){
            response = enterpriseRepository.findByCreatedAtBetweenAndEnterpriseNameContaining(startDateTime, endDateTime, enterpriseName, pageable);
        } else if(country != null){
            response = enterpriseRepository.findByCreatedAtBetweenAndCountry(startDateTime, endDateTime, country, pageable);
        } else {
            response = enterpriseRepository.findByCreatedAtBetween(startDateTime, endDateTime, pageable);
        }
        response.forEach(enterprise -> {
            PausingStatus pausingStatus = isPaused(enterprise.getEnterpriseId(), "enterprise");
            enterprise.setPausingStatus(pausingStatus);
        });

        return response;
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

        Map<String, String> messageMap = new LinkedHashMap<>();

        messageMap.put("userId", request.getUserId());
        messageMap.put("pauseDay", String.valueOf(request.getPauseDay()));

        MessageDto messageDto = sqsSender.createMessageDtoFromRequest("pause user", messageMap);

        sqsSender.sendToSQS(messageDto);

        return AdminDto.PauseUserResponse.builder()
                .pauseStartAt(LocalDateTime.now())
                .build();
    }

    public AdminDto.UnpauseUserResponse unpauseUser(AdminDto.UnpauseUserRequest request) {
        String key = "pause:user:" + request.getUserId();
        redisTemplate.delete(key);

        Map<String, String> messageMap = new LinkedHashMap<>();

        messageMap.put("userId", request.getUserId());

        MessageDto messageDto = sqsSender.createMessageDtoFromRequest("unpause user", messageMap);

        sqsSender.sendToSQS(messageDto);

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

    public Page<UserDto.ReadResponse> searchUsers(String gender, String email, String country, String phoneNumber, LocalDateTime startDateTime, LocalDateTime endDateTime, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<UserDto.ReadResponse> response;

        // 조건에 따른 분기 처리
        if (gender != null) {
            response = userRepository.findUserByGender(startDateTime, endDateTime, gender, pageable);
        } else if (email != null) {
            response = userRepository.findUserByEmailContaining(startDateTime, endDateTime, email, pageable);
        } else if (country != null) {
            response = userRepository.findUserByCountryContaining(startDateTime, endDateTime, country, pageable);
        } else if (phoneNumber != null) {
            response = userRepository.findUserByPhoneNumberContaining(startDateTime, endDateTime, phoneNumber, pageable);
        } else {
            response = userRepository.findAllUser(startDateTime, endDateTime, pageable);
        }

        response.forEach(user -> {
            PausingStatus pausingStatus = isPaused(user.getUserId(), "user");
            user.setPausingStatus(pausingStatus);
        });

        return response;
    }


    public Page<UserDto.ReadUserSkinResponse> findUserSkinByUserId(String userId, int page, int size) {
        final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return userSkinRepository.findUserSkinByUserId(UUID.fromString(userId), pageable);
    }

    @Transactional
    public UserDto.DeleteUserPostResponse deleteUserPost(UserDto.DeleteUserPostRequest request) {
        UserSkin userSkin = userSkinRepository.findById(UUID.fromString(request.getPostId())).orElseThrow(() -> new NoSuchElementException("UserPost not found"));

        userSkinRepository.delete(userSkin);

        Map<String, String> messageMap = new LinkedHashMap<>();

        messageMap.put("postId", request.getPostId());
        messageMap.put("userId", request.getUserId());

        MessageDto messageDto = sqsSender.createMessageDtoFromRequest("delete user post", messageMap);

        sqsSender.sendToSQS(messageDto);

        return UserDto.DeleteUserPostResponse.builder().deletedAt(LocalDateTime.now()).build();
    }


    @Transactional
    public UserDto.DeleteResponse deleteUser(UserDto.DeleteUserRequest request) {
        User user = userRepository.findById(UUID.fromString(request.getUserId())).orElseThrow(() -> new NoSuchElementException("User not found"));
        userRepository.delete(user);

        Map<String, String> messageMap = new LinkedHashMap<>();

        messageMap.put("userId", request.getUserId());

        MessageDto messageDto = sqsSender.createMessageDtoFromRequest("delete user", messageMap);

        sqsSender.sendToSQS(messageDto);

        return UserDto.DeleteResponse.builder().deletedAt(LocalDateTime.now()).build();
    }

    public Page<UserDto.ReadUserSkinResponse> searchUserSkins(String userId, String country, LocalDateTime startDateTime, LocalDateTime endDateTime, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (country != null) {
            return userSkinRepository.findUserSkinByCountryContaining(startDateTime, endDateTime, country, pageable);
        } else {
            return userSkinRepository.findUserSkinByCreatedAtBetween(startDateTime, endDateTime, pageable);
        }

    }

    public UserDto.ReadResponse findUserById(String userId) {

        UserDto.ReadResponse response = userRepository.findUserById(UUID.fromString(userId)).orElse(null);

        if(response == null) {
            throw new NoSuchElementException("User not found");
        }

        return response;
    }

    public PausingStatus isPaused(UUID id, String type) {
        String key = null;

        if(type.equals("user")){
            key = "pause:user:" + id.toString();
        }else if(type.equals("enterprise")) {
            key = "pause:enterprise:" + id.toString();
        }

        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return PausingStatus.builder().isPaused(false).build();
        }

        Long expireSeconds = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (expireSeconds == null || expireSeconds == -1) {
            return PausingStatus.builder()
                    .isPaused(true)
                    .days(0)
                    .hours(0)
                    .build();
        } else {
            long days = TimeUnit.SECONDS.toDays(expireSeconds);
            long hours = TimeUnit.SECONDS.toHours(expireSeconds) - TimeUnit.DAYS.toHours(days);
            return PausingStatus.builder()
                    .isPaused(true)
                    .days((int) days)
                    .hours((int) hours)
                    .build();
        }
    }
}
