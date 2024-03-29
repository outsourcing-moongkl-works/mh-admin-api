package org.outsourcing.mhadminapi.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.auth.UserPrincipal;
import org.outsourcing.mhadminapi.dto.EnterpriseDto;
import org.outsourcing.mhadminapi.dto.ResponseDto;
import org.outsourcing.mhadminapi.service.EnterpriseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/enterprise")
public class EnterpriseController {
    private final EnterpriseService enterpriseService;

    @PostMapping(value = "/authorize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto> requestApproveEnterprise(
            @RequestPart(name = "authorize_enterprise_request") EnterpriseDto.AuthorizeRequest request,
            @RequestPart(name = "logo_img") MultipartFile logoImg) {
        log.info("authorizeEnterprise: {}", request);

        enterpriseService.requestApproveEnterprise(request, logoImg);

        // Assuming ResponseDto has a proper constructor or method to create a success response
        // Change HttpStatus.NO_CONTENT to HttpStatus.OK or appropriate status based on your logic
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //가입 상태확인
    @GetMapping("/approval")
    public ResponseEntity<EnterpriseDto.GetApprovalResponse> getApproval(@RequestBody String loginId) {
        log.info("getApproval: {}", loginId);

        EnterpriseDto.GetApprovalResponse response = enterpriseService.getApproval(loginId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PreAuthorize("hasAuthority('ENTERPRISE')")
    @DeleteMapping("/withdraw")
    public ResponseEntity<ResponseDto> withdraw(@AuthenticationPrincipal UserPrincipal enterprisePrincipal, @RequestBody String password) {
        log.info("withdraw: {}", enterprisePrincipal);

        enterpriseService.withdraw(enterprisePrincipal.getEnterprise().getId(), password);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/login")
    public ResponseEntity<EnterpriseDto.LoginResponse> login(@RequestBody EnterpriseDto.LoginRequest request) {

        log.info("login: {}", request);

        EnterpriseDto.LoginResponse response = enterpriseService.login(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAuthority('ENTERPRISE')")
    @GetMapping("/test")
    public ResponseEntity<ResponseDto> test() {

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
