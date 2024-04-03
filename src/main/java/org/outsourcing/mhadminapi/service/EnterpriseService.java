package org.outsourcing.mhadminapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.auth.JwtTokenProvider;
import org.outsourcing.mhadminapi.dto.AdminDto;
import org.outsourcing.mhadminapi.dto.EnterpriseDto;
import org.outsourcing.mhadminapi.dto.JwtDto;
import org.outsourcing.mhadminapi.dto.MessageDto;
import org.outsourcing.mhadminapi.entity.Enterprise;
import org.outsourcing.mhadminapi.entity.LogoImgUrl;
import org.outsourcing.mhadminapi.entity.Story;
import org.outsourcing.mhadminapi.entity.StoryImgUrl;
import org.outsourcing.mhadminapi.exception.EnterpriseBlockException;
import org.outsourcing.mhadminapi.exception.EnterpriseErrorResult;
import org.outsourcing.mhadminapi.exception.EnterpriseException;
import org.outsourcing.mhadminapi.repository.EnterpriseRepository;
import org.outsourcing.mhadminapi.repository.LogoImgUrlRepository;
import org.outsourcing.mhadminapi.repository.StoryRepository;
import org.outsourcing.mhadminapi.sqs.SqsSender;
import org.outsourcing.mhadminapi.vo.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final LogoImgService logoImgService;
    private final StoryRepository storyRepository;
    private final LogoImgUrlRepository logoImgUrlRepository;
    private final StoryService storyService;
    private final StringRedisTemplate redisTemplate;
    private final SqsSender sqsSender;

    @Transactional
    public void requestApproveEnterprise(EnterpriseDto.AuthorizeRequest request, MultipartFile logoImg) {

        Enterprise enterprise = Enterprise.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf("ENTERPRISE"))
                .corporateNumber(request.getCorporateNumber())
                .businessNumber(request.getBusinessNumber())
                .name(request.getName())
                .address(request.getAddress())
                .managerEmail(request.getManagerEmail())
                .managerName(request.getManagerName())
                .managerPhone(request.getManagerPhone())
                .isApproved(false)
                .country(request.getCountry())
                .build();

        enterprise.setEnterpriseId();
        EnterpriseDto.LogoImgUrl logoImgUrl = logoImgService.uploadLogoImg(enterprise.getId(), logoImg);
        enterprise.updateLogoImgUrl(LogoImgUrl.convertLogoImgUrlDtoToEntity(logoImgUrl));

        enterpriseRepository.save(enterprise);
    }

    @Transactional
    public EnterpriseDto.LoginResponse login(EnterpriseDto.LoginRequest request) {
        // 이메일(또는 로그인 ID)을 기반으로 사용자 정보 조회
        Enterprise enterprise = enterpriseRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new EnterpriseException(EnterpriseErrorResult.ENTERPRISE_NOT_FOUND));

        // matches 메서드를 사용하여 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), enterprise.getPassword())) {
            throw new EnterpriseException(EnterpriseErrorResult.WRONG_PASSWORD);
        }

        // 승인 상태인지 확인, 승인 상태가 아니라면 예외 발생
        if (!enterprise.getIsApproved()) {
            throw new EnterpriseException(EnterpriseErrorResult.NOT_APPROVED_ENTERPRISE);
        }

        // redis를 통해 정지 상태인지 확인, 정지 상태라면 예외 발생
        checkAndThrowIfPaused(enterprise.getId()); // 이 메서드 호출로 정지 상태 확인

        JwtDto.JwtRequestDto jwtRequestDto = JwtDto.JwtRequestDto.builder()
                .id(enterprise.getId().toString())
                .email(request.getLoginId())
                .role(enterprise.getRole().name())
                .build();

        String accessToken = jwtTokenProvider.createAccessToken(jwtRequestDto);

        return EnterpriseDto.LoginResponse.builder()
                .accessToken(accessToken)
                .createdAt(String.valueOf(LocalDateTime.now()))
                .build();
    }

    @Transactional
    public EnterpriseDto.GetApprovalResponse getApproval(String loginId) {

        Enterprise enterprise = enterpriseRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new EnterpriseException(EnterpriseErrorResult.ENTERPRISE_NOT_FOUND));

            return EnterpriseDto.GetApprovalResponse.builder()
                    .isApproved(enterprise.getIsApproved())
                    .build();
    }

    @Transactional
    public void withdraw(UUID id, String password) {

            Enterprise enterprise = enterpriseRepository.findById(id)
                    .orElseThrow(() -> new EnterpriseException(EnterpriseErrorResult.ENTERPRISE_NOT_FOUND));

            if(!passwordEncoder.matches(password, enterprise.getPassword())){
                throw new EnterpriseException(EnterpriseErrorResult.WRONG_PASSWORD);
            }

            enterpriseRepository.delete(enterprise);
    }

    @Transactional
    public void createStory(UUID enterpriseId, MultipartFile storyImg) {
        Enterprise enterprise = enterpriseRepository.findById(enterpriseId).orElseThrow(() -> new EnterpriseException(EnterpriseErrorResult.ENTERPRISE_NOT_FOUND));

        EnterpriseDto.StoryUrl storyImgUrlDto = storyService.uploadStoryImg(enterpriseId, storyImg);

        Story story = Story.builder()
                .isPublic(true)
                .build();

        story.setEnterprise(enterprise);
        story.setStoryImgUrl(StoryImgUrl.convertStoryImgUrlDtoToEntity(storyImgUrlDto));

        storyRepository.save(story);

        Map<String, String> messageMap = new LinkedHashMap<>();
        messageMap.put("storyId", story.getId().toString());
        messageMap.put("enterpriseId", enterpriseId.toString());
        messageMap.put("s3Url", storyImgUrlDto.getS3Url());
        messageMap.put("cloudfrontUrl", storyImgUrlDto.getCloudfrontUrl());
    }

    @Transactional
    public void deleteStory(UUID storyId) {
        Story story = storyRepository.findById(storyId).orElseThrow(() -> new EnterpriseException(EnterpriseErrorResult.STORY_NOT_FOUND));

        storyRepository.deleteById(storyId);
    }

    public Page<EnterpriseDto.GetStoryPageResponse> searchStory(UUID enterpriseId, int page, int size, String sort, LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean isPublic) {
        Pageable pageable;
        if (sort == null || sort.isEmpty() || sort.equals("createdAt")) {
            pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        } else {
            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        }

        if(isPublic == null) {
            return storyRepository.findByEnterpriseIdAndCreatedAtBetween(enterpriseId, startDateTime, endDateTime, pageable);
        }else if(isPublic.equals(true)){
            return storyRepository.findByEnterpriseIdAndCreatedAtBetweenAndPublic(enterpriseId, startDateTime, endDateTime, pageable);
        }else {
            return storyRepository.findByEnterpriseIdAndCreatedAtBetweenAndNotPublic(enterpriseId, startDateTime, endDateTime, pageable);
        }
    }

    @Transactional
    public void changeIsPublic(String storyId) {
        Story story = storyRepository.findById(UUID.fromString(storyId)).orElseThrow(() -> new EnterpriseException(EnterpriseErrorResult.STORY_NOT_FOUND));

        story.changeIsPublic();

        storyRepository.save(story);

        Map<String, String> messageMap = new LinkedHashMap<>();
        messageMap.put("id", story.getId().toString());

        MessageDto messageDto = sqsSender.createMessageDtoFromRequest("update story visible", messageMap);

        sqsSender.sendToSQS(messageDto);

    }

    // 정지 상태 확인 및 예외 처리 메서드
    public void checkAndThrowIfPaused(UUID enterpriseId) {
        String key = "pause:enterprise:" + String.valueOf(enterpriseId);
        String value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            // 키의 남은 만료 시간(초 단위) 조회
            Long expireSeconds = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            String message;
            if (expireSeconds == null) {
                message = "영구정지된 기업입니다.";
            } else {
                // 남은 만료 시간을 일수와 시간으로 계산
                long days = TimeUnit.SECONDS.toDays(expireSeconds);
                long hours = TimeUnit.SECONDS.toHours(expireSeconds) - TimeUnit.DAYS.toHours(days);
                message = String.format("정지 기한이 %d일 %d시간 남았습니다.", days, hours);
            }

            throw new EnterpriseBlockException(message);
        }
    }

}
