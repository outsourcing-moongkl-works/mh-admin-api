package org.outsourcing.mhadminapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.auth.JwtTokenProvider;
import org.outsourcing.mhadminapi.dto.EnterpriseDto;
import org.outsourcing.mhadminapi.dto.JwtDto;
import org.outsourcing.mhadminapi.entity.Enterprise;
import org.outsourcing.mhadminapi.entity.LogoImgUrl;
import org.outsourcing.mhadminapi.entity.Story;
import org.outsourcing.mhadminapi.exception.EnterpriseErrorResult;
import org.outsourcing.mhadminapi.exception.EnterpriseException;
import org.outsourcing.mhadminapi.repository.EnterpriseRepository;
import org.outsourcing.mhadminapi.repository.LogoImgUrlRepository;
import org.outsourcing.mhadminapi.repository.StoryRepository;
import org.outsourcing.mhadminapi.vo.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

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

        // 이메일을 기반으로 사용자 정보 조회
        Enterprise enterprise = enterpriseRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new EnterpriseException(EnterpriseErrorResult.ENTERPRISE_NOT_FOUND));

        // matches 메서드를 사용하여 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), enterprise.getPassword())) {
            throw new EnterpriseException(EnterpriseErrorResult.ENTERPRISE_NOT_FOUND);
        }

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


    public EnterpriseDto.GetApprovalResponse getApproval(String loginId) {

        Enterprise enterprise = enterpriseRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new EnterpriseException(EnterpriseErrorResult.ENTERPRISE_NOT_FOUND));

            return EnterpriseDto.GetApprovalResponse.builder()
                    .isApproved(enterprise.getIsApproved())
                    .build();
    }

    public void withdraw(UUID id, String password) {

            Enterprise enterprise = enterpriseRepository.findById(id)
                    .orElseThrow(() -> new EnterpriseException(EnterpriseErrorResult.ENTERPRISE_NOT_FOUND));

            if(!passwordEncoder.matches(password, enterprise.getPassword())){
                throw new EnterpriseException(EnterpriseErrorResult.WRONG_PASSWORD);
            }

            enterpriseRepository.delete(enterprise);
    }

    public void createStory(UUID enterpriseId, MultipartFile storyImg) {

    }

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

    public void changeIsPublic(String storyId) {
        Story story = storyRepository.findById(UUID.fromString(storyId)).orElseThrow(() -> new EnterpriseException(EnterpriseErrorResult.STORY_NOT_FOUND));

        story.changeIsPublic();

        storyRepository.save(story);
    }
}
