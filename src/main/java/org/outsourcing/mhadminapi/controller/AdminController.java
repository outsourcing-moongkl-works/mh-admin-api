package org.outsourcing.mhadminapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.outsourcing.mhadminapi.dto.AdminDto;
import org.outsourcing.mhadminapi.exception.AdminErrorResult;
import org.outsourcing.mhadminapi.exception.AdminException;
import org.outsourcing.mhadminapi.repository.AdminRepository;
import org.outsourcing.mhadminapi.service.AdminService;
import org.outsourcing.mhadminapi.vo.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AdminRepository adminRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

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

    //login with redis data session
    @PostMapping("/admin/login")
    public ResponseEntity<AdminDto.LoginAdminResponse> login(@RequestBody AdminDto.LoginAdminRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getAdminId(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // HttpServletRequest 객체를 사용하여 세션에 접근
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            // 로그인 성공 로직 처리...
        } catch (AuthenticationException e) {
            // 로그인 실패 처리...
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AdminDto.LoginAdminResponse response = adminService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PostMapping("/admin/logout")
    public String logout() {
        return "logout";
    }

    //회원가입 전 승인 요청

}
