package org.outsourcing.mhadminapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.dto.*;
import org.outsourcing.mhadminapi.exception.AdminErrorResult;
import org.outsourcing.mhadminapi.exception.AdminException;
import org.outsourcing.mhadminapi.service.AdminService;
import org.outsourcing.mhadminapi.vo.Role;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
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

    @PostMapping("/login")
    public ResponseEntity<AdminDto.LoginAdminResponse> login(@RequestBody AdminDto.LoginAdminRequest request) {
        AdminDto.LoginAdminResponse response = adminService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAuthority('MASTER')")
    @DeleteMapping()
    public ResponseEntity<AdminDto.DeleteAdminResponse> deleteAdmin(@RequestParam(name = "admin_id") String adminId){

        AdminDto.DeleteAdminResponse response = adminService.deleteAdmin(adminId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //!===============기업 관리 API==================!//
    @PreAuthorize("hasAuthority('MASTER')")
    @PostMapping("/approve")
    public ResponseEntity<ResponseDto> approveEnterprise(@RequestBody AdminDto.ApproveEnterpriseRequest request){
        log.info("approveEnterprise: {}", request);

        adminService.approveEnterprise(request.getEnterpriseId());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
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
    @PostMapping("/enterprise/pausing")
    public ResponseEntity<AdminDto.PauseEnterpriseResponse> pauseEnterprise(@RequestBody AdminDto.PauseEnterpriseRequest request){
        log.info("pauseEnterprise: {}", request.getEnterpriseId());

        AdminDto.PauseEnterpriseResponse response = adminService.pauseEnterprise(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //유저 사용정지
    @PreAuthorize("hasAuthority('MASTER')")
    @PostMapping("/user/pausing")
    public ResponseEntity<AdminDto.PauseUserResponse> pauseUser(@RequestBody AdminDto.PauseUserRequest request){
        log.info("pauseUser: {}", request.getUserId());

        AdminDto.PauseUserResponse response = adminService.pauseUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('MASTER')")
    @PostMapping("/enterprise/unpausing")
    public ResponseEntity<AdminDto.UnpauseEnterpriseResponse> unpauseEnterprise(@RequestBody AdminDto.PauseEnterpriseRequest request){
        log.info("unpauseEnterprise: {}", request.getEnterpriseId());

        AdminDto.UnpauseEnterpriseResponse response = adminService.unpauseEnterprise(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('MASTER')")
    @PostMapping("/user/unpausing")
    public ResponseEntity<AdminDto.UnpauseUserResponse> unpauseUser(@RequestBody AdminDto.UnpauseUserRequest request){
        log.info("unpauseUser: {}", request.getUserId());

        AdminDto.UnpauseUserResponse response = adminService.unpauseUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('MASTER')")
    @GetMapping("/enterprise/searching")
    public ResponseEntity<Page<EnterpriseDto.ReadResponse>> searchEnterprises(
            @RequestParam(required = false, defaultValue = "1900-01-01") String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String enterpriseName,
            @RequestParam("page") int page, @RequestParam("size") int size) {

        if (endDate == null || endDate.trim().isEmpty()) {
            endDate = LocalDate.now().toString();
        }

        log.info("Searching Enterprises: Date Range: {} - {}, Country: {}, Name: {}", startDate, endDate, country, enterpriseName);

        LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);

        Page<EnterpriseDto.ReadResponse> response = adminService.searchEnterprises(startDateTime, endDateTime, country, enterpriseName, page, size);

        return ResponseEntity.ok(response);
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/searching")
    public ResponseEntity<Page<UserDto.ReadResponse>> searchUsers(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false, defaultValue = "1900-01-01") String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam int page,
            @RequestParam int size) {

        if (endDate == null || endDate.trim().isEmpty()) {
            endDate = LocalDate.now().toString();
        }

        log.info("Searching Users: Date Range: {} - {}, Country: {}, Name: {}", startDate, endDate, country, email);

        LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);

        Page<UserDto.ReadResponse> response = adminService.searchUsers(
                gender, email, country, phoneNumber, startDateTime, endDateTime, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user-skin/searching") //userId, country, start, end, page, size
    public ResponseEntity<Page<UserDto.ReadUserSkinResponse>> searchUserSkins(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String country,
            @RequestParam(required = false, defaultValue = "1900-01-01") String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam int page,
            @RequestParam int size) {

        if (endDate == null || endDate.trim().isEmpty()) {
            endDate = LocalDate.now().toString();
        }

        log.info("Searching UserSkins: Date Range: {} - {}, Country: {}, userId: {}", startDate, endDate, country, userId);

        LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);

        Page<UserDto.ReadUserSkinResponse> response = adminService.searchUserSkins(userId, country, startDateTime, endDateTime, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    //회원별 등록한 here보기
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user-skin")
    public ResponseEntity<Page<UserDto.ReadUserSkinResponse>> findUserSkinByUserId(@RequestParam String userId, @RequestParam int page, @RequestParam int size) {

        Page<UserDto.ReadUserSkinResponse> response = adminService.findUserSkinByUserId(userId, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAuthority('MASTER')")
    @DeleteMapping("/user-post")
    public ResponseEntity<UserDto.DeleteUserPostResponse> deleteUserPost(@RequestBody UserDto.DeleteUserPostRequest request) {

        UserDto.DeleteUserPostResponse response = adminService.deleteUserPost(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('MASTER')")
    @DeleteMapping("/user")
    public ResponseEntity<UserDto.DeleteResponse> deleteUser(@RequestBody UserDto.DeleteUserRequest request) {

        UserDto.DeleteResponse response = adminService.deleteUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto.ReadResponse> findUserById(@PathVariable String userId) {

        UserDto.ReadResponse response = adminService.findUserById(userId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
