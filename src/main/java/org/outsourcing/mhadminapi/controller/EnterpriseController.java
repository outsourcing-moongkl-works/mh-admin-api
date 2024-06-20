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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/enterprise")
public class EnterpriseController {
    private final EnterpriseService enterpriseService;

    @PostMapping(value = "/approve", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto> requestApproveEnterprise(
            @RequestPart(name = "authorize_enterprise_request") EnterpriseDto.AuthorizeRequest request,
            @RequestPart(name = "logo_img") MultipartFile logoImg) {
        log.info("authorizeEnterprise: {}", request);

        enterpriseService.requestApproveEnterprise(request, logoImg);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //가입 상태확인
    @GetMapping("/approval")
    public ResponseEntity<EnterpriseDto.GetApprovalResponse> getApproval(@RequestParam String loginId) {
        log.info("getApproval: {}", loginId);

        EnterpriseDto.GetApprovalResponse response = enterpriseService.getApproval(loginId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PreAuthorize("hasAuthority('ENTERPRISE')")
    @DeleteMapping("/withdraw")
    public ResponseEntity<ResponseDto> withdraw(@AuthenticationPrincipal UserPrincipal enterprisePrincipal, @RequestBody EnterpriseDto.WithdrawRequest request) {
        log.info("withdraw: {}", enterprisePrincipal);

        enterpriseService.withdraw(enterprisePrincipal.getEnterprise().getId(), request.getPassword());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/login")
    public ResponseEntity<EnterpriseDto.LoginResponse> login(@RequestBody EnterpriseDto.LoginRequest request) {

        log.info("login: {}", request);

        EnterpriseDto.LoginResponse response = enterpriseService.login(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //등록일자별(사이) Story 조회, 기간 내 Story 개수 포함 응답


    @PreAuthorize("hasAuthority('ENTERPRISE')")
    @PostMapping(value = "/story", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto> createStory(
            @AuthenticationPrincipal UserPrincipal enterprisePrincipal,
            @RequestPart(name = "story_img") MultipartFile storyImg) {

        UUID enterpriseId = enterprisePrincipal.getEnterprise().getId();

        log.info("createStory: {}", enterpriseId);

        enterpriseService.createStory(enterpriseId, storyImg);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //Story 비공개
    @PreAuthorize("hasAuthority('ENTERPRISE')")
    @PostMapping("/story/changing-public")
    public ResponseEntity<ResponseDto> changeIsPublic(@RequestBody EnterpriseDto.ChangeIsPublicRequest request) {
        log.info("changeIsPublic: {}", request);
        enterpriseService.changeIsPublic(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //Story 삭제
    @PreAuthorize("hasAuthority('ENTERPRISE')")
    @DeleteMapping("/story")
    public ResponseEntity<ResponseDto> deleteStory(@RequestBody EnterpriseDto.DeleteStoryRequest request) {
        log.info("deleteStory: {}", request.getStoryId());

        enterpriseService.deleteStory(UUID.fromString(request.getStoryId()));

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("hasAuthority('ENTERPRISE')")
    @GetMapping("/story/searching")
    public ResponseEntity<Page<EnterpriseDto.GetStoryPageResponse>> searchStories(
            @AuthenticationPrincipal UserPrincipal enterprisePrincipal,
            @Parameter(description = "시작일자(YYYY-MM-DD)", schema = @Schema(type = "string", format = "date")) @RequestParam(defaultValue = "1900-01-01") String startDate,
            @Parameter(description = "종료일자(YYYY-MM-DD)", schema = @Schema(type = "string", format = "date")) @RequestParam(required = false) String endDate,
            @Parameter(description = "페이지 번호", schema = @Schema(type = "integer")) @RequestParam(required = false) int page,
            @Parameter(description = "페이지 크기", schema = @Schema(type = "integer")) @RequestParam(required = false) int size,
            @Parameter(description = "정렬 기준(useCount, shareCount, viewCount, 없으면 createdAt)", schema = @Schema(type = "string")) @RequestParam(required = false) String sort,
            @Parameter(description = "공개 여부", schema = @Schema(type = "boolean")) @RequestParam(required = false) Boolean isPublic){

        log.info("getStories");

        UUID enterpriseId = enterprisePrincipal.getEnterprise().getId();
        Page<EnterpriseDto.GetStoryPageResponse> response = null;

        if (endDate == null || endDate.trim().isEmpty()) {
            endDate = LocalDate.now().toString();
        }

        LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);

        response = enterpriseService.searchStory(enterpriseId, page, size, sort, startDateTime, endDateTime, isPublic);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /*
    기업정보수정
     */
    @PreAuthorize("hasAuthority('ENTERPRISE')")
    @PutMapping(value = "/info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto> updateEnterpriseInfo(
            @AuthenticationPrincipal UserPrincipal enterprisePrincipal,
            @RequestPart(name = "update_enterprise_info") EnterpriseDto.UpdateInfoRequest request,
            @RequestPart(name = "logo_img", required = false) MultipartFile logoImg) {
        log.info("updateEnterpriseInfo: {}", request);

        enterpriseService.updateEnterpriseInfo(enterprisePrincipal.getEnterprise().getId(), request, logoImg);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
