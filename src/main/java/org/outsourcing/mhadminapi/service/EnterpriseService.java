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

    public void authorizeEnterprise(EnterpriseDto.AuthorizeRequest request, MultipartFile logoImg) {

        Enterprise enterprise = Enterprise.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf("ENTERPRISE"))
                .corporateNumber(request.getCorporateNumber())
                .name(request.getName())
                .address(request.getAddress())
                .managerEmail(request.getManagerEmail())
                .managerName(request.getManagerName())
                .managerPhone(request.getManagerPhone())
                .isApproved("FALSE")
                .build();

        logoImgService.uploadLogoImg(enterprise.getId(), logoImg);

        enterpriseRepository.save(enterprise);
    }

    @Transactional
    public EnterpriseDto.LoginResponse login(EnterpriseDto.LoginRequest request) {

        Enterprise enterprise = enterpriseRepository.findByEmailAndPassword(request.getLoginId(), passwordEncoder.encode(request.getPassword()))
                .orElseThrow(() -> new EnterpriseException(EnterpriseErrorResult.ENTERPRISE_NOT_FOUND));

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
