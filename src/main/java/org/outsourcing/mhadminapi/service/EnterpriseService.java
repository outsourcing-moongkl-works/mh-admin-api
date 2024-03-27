package org.outsourcing.mhadminapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.auth.JwtTokenProvider;
import org.outsourcing.mhadminapi.dto.EnterpriseDto;
import org.outsourcing.mhadminapi.dto.JwtDto;
import org.outsourcing.mhadminapi.entity.Enterprise;
import org.outsourcing.mhadminapi.exception.EnterpriseErrorResult;
import org.outsourcing.mhadminapi.exception.EnterpriseException;
import org.outsourcing.mhadminapi.repository.EnterpriseRepository;
import org.outsourcing.mhadminapi.vo.Role;
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
    private final EnterpriseStoryService enterpriseStoryService;

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
                .isApproved("FALSE")
                .country(request.getCountry())
                .build();

        enterprise.setEnterpriseId();

        enterpriseRepository.save(enterprise);

        logoImgService.uploadLogoImg(enterprise.getId(), logoImg);

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
                .email(request.getLoginId())
                .role(enterprise.getRole().name())
                .build();

        String accessToken = jwtTokenProvider.createAccessToken(jwtRequestDto);

        return EnterpriseDto.LoginResponse.builder()
                .accessToken(accessToken)
                .createdAt(String.valueOf(LocalDateTime.now()))
                .build();
    }

    public void approveEnterprise(String enterpriseId) {

            Enterprise enterprise = enterpriseRepository.findById(UUID.fromString(enterpriseId))
                    .orElseThrow(() -> new EnterpriseException(EnterpriseErrorResult.ENTERPRISE_NOT_FOUND));

            enterprise.updateIsApproved("TRUE");

            enterpriseRepository.save(enterprise);
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
}
