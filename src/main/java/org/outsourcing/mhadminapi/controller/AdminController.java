package org.outsourcing.mhadminapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.auth.UserPrincipal;
import org.outsourcing.mhadminapi.dto.AdminDto;
import org.outsourcing.mhadminapi.dto.EnterpriseDto;
import org.outsourcing.mhadminapi.dto.ResponseDto;
import org.outsourcing.mhadminapi.exception.AdminErrorResult;
import org.outsourcing.mhadminapi.exception.AdminException;
import org.outsourcing.mhadminapi.repository.AdminRepository;
import org.outsourcing.mhadminapi.service.AdminService;
import org.outsourcing.mhadminapi.service.EnterpriseService;
import org.outsourcing.mhadminapi.vo.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final EnterpriseService enterpriseService;
    //추후 배포 시 create admin은 master만 가능하도록 변경
    @PreAuthorize("hasAuthority('MASTER')")
    @PostMapping
    public ResponseEntity<AdminDto.CreateAdminResponse> createAdmin(@RequestBody AdminDto.CreateAdminRequest request) {

        String adminRole = request.getRole();

        if (!Role.isValidRole(adminRole)) {
            throw new AdminException(AdminErrorResult.INVALID_ROLE);
        }

        AdminDto.CreateAdminResponse response = adminService.createAdmin(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //login with redis data session
    @PostMapping("/login")
    public ResponseEntity<AdminDto.LoginAdminResponse> login(@RequestBody AdminDto.LoginAdminRequest request) {
        AdminDto.LoginAdminResponse response = adminService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAuthority('MASTER')")
    @DeleteMapping("/adminId")
    public ResponseEntity<AdminDto.DeleteAdminResponse> deleteAdmin(@RequestParam(name = "admin_id") String adminId){

        AdminDto.DeleteAdminResponse response = adminService.deleteAdmin(adminId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAuthority('MASTER')")
    @PostMapping("/approve")
    public ResponseEntity<ResponseDto> approveEnterprise(@RequestBody AdminDto.ApproveEnterpriseRequest request){
        log.info("approveEnterprise: {}", request);

        adminService.approveEnterprise(request.getEnterpriseId());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    //!===============기업 관리 API==================!//
    //기업 강퇴
    @PreAuthorize("hasAuthority('MASTER')")
    @DeleteMapping("/enterprise")
    public ResponseEntity<ResponseDto> deleteEnterprise(@RequestParam("enterprise_id") String enterpriseId){
        log.info("deleteEnterprise: {}", enterpriseId);

        adminService.deleteEnterprise(enterpriseId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //기업 사용 정지
    @PreAuthorize("hasAuthority('MASTER')")
    @PostMapping("/enterprise/pause")
    public ResponseEntity<AdminDto.PauseEnterpriseResponse> pauseEnterprise(@RequestBody AdminDto.PauseEnterpriseRequest request){
        log.info("pauseEnterprise: {}", request.getEnterpriseId());

        AdminDto.PauseEnterpriseResponse response = adminService.pauseEnterprise(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //유저 사용정지
    @PreAuthorize("hasAuthority('MASTER')")
    @PostMapping("/user/pause")
    public ResponseEntity<AdminDto.PauseUserResponse> pauseUser(@RequestBody AdminDto.PauseUserRequest request){
        log.info("pauseUser: {}", request.getUserId());

        AdminDto.PauseUserResponse response = adminService.pauseUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('MASTER')")
    @PostMapping("/enterprise/unpause")
    public ResponseEntity<AdminDto.UnpauseEnterpriseResponse> unpauseEnterprise(@RequestBody AdminDto.PauseEnterpriseRequest request){
        log.info("pauseEnterprise: {}", request.getEnterpriseId());

        AdminDto.UnpauseEnterpriseResponse response = adminService.unpauseEnterprise(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('MASTER')")
    @PostMapping("/user/unpause")
    public ResponseEntity<AdminDto.UnpauseUserResponse> unpauseUser(@RequestBody AdminDto.PauseUserRequest request){
        log.info("pauseUser: {}", request.getUserId());

        AdminDto.UnpauseUserResponse response = adminService.unpauseUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //모든 Story get, 페이징
    @PreAuthorize("hasAuthority('MASTER')")
    @GetMapping("/story")
    public ResponseEntity<ResponseDto> getStory(@RequestParam("page") int page, @RequestParam("size") int size){
        log.info("getStory: {} ~ {}", page, size);

        //enterpriseService.getStory(page, size);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasAuthority('MASTER')")
    @GetMapping("/enterprise/search")
    public ResponseEntity<Page<EnterpriseDto.GetEnterprisePageResponse>> searchEnterprises(
            @RequestParam(required = false, defaultValue = "1900-01-01") String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String enterpriseName,
            @RequestParam("page") int page, @RequestParam("size") int size) {

        log.info("Searching Enterprises: Date Range: {} - {}, Country: {}, Name: {}", startDate, endDate, country, enterpriseName);

        if (endDate == null || endDate.trim().isEmpty()) {
            endDate = LocalDate.now().toString();
        }

        Page<EnterpriseDto.GetEnterprisePageResponse> response = null;

        LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);

        if (startDate != null && endDate != null) {
                response = adminService.searchEnterprises(startDateTime, endDateTime, country, enterpriseName, page, size);
        }
        return ResponseEntity.ok(response);
    }
}
