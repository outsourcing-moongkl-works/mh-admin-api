package org.outsourcing.mhadminapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.auth.UserPrincipal;
import org.outsourcing.mhadminapi.dto.AdminDto;
import org.outsourcing.mhadminapi.dto.ResponseDto;
import org.outsourcing.mhadminapi.exception.AdminErrorResult;
import org.outsourcing.mhadminapi.exception.AdminException;
import org.outsourcing.mhadminapi.repository.AdminRepository;
import org.outsourcing.mhadminapi.service.AdminService;
import org.outsourcing.mhadminapi.service.EnterpriseService;
import org.outsourcing.mhadminapi.vo.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/approve/{enterprise_id}")
    public ResponseEntity<ResponseDto> approveEnterprise(@RequestParam("enterprise_id") String enterpriseId){
        log.info("approveEnterprise: {}", enterpriseId);

        enterpriseService.approveEnterprise(enterpriseId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



}
