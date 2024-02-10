package org.outsourcing.mhadminapi.controller;

import lombok.RequiredArgsConstructor;
import org.outsourcing.mhadminapi.dto.AdminDto;
import org.outsourcing.mhadminapi.exception.AdminErrorResult;
import org.outsourcing.mhadminapi.exception.AdminException;
import org.outsourcing.mhadminapi.repository.AdminRepository;
import org.outsourcing.mhadminapi.service.AdminService;
import org.outsourcing.mhadminapi.vo.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AdminRepository adminRepository;
    @PostMapping("/admin/sign-up")
    public ResponseEntity<AdminDto.CreateAdminResponse> signUp(@RequestBody AdminDto.CreateAdminRequest request) {
        //orElseThrow
        if(adminRepository.existsByAdminId(request.getAdminId())){
            throw new AdminException(AdminErrorResult.ALREADY_EXIST_ADMIN);
        }

        String adminRole = request.getRole();

        if (!Role.isValidRole(adminRole)) {
            throw new AdminException(AdminErrorResult.INVALID_ROLE);
        }

        AdminDto.CreateAdminResponse response = adminService.createAdmin(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
//    @PostMapping("/admin/login")

    @PostMapping("/admin/logout")
    public String logout() {
        return "logout";
    }

    //회원가입 전 승인 요청

}
