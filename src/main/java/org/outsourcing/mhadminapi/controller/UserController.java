package org.outsourcing.mhadminapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.dto.AdminDto;
import org.outsourcing.mhadminapi.dto.UserDto;
import org.outsourcing.mhadminapi.exception.AdminErrorResult;
import org.outsourcing.mhadminapi.exception.AdminException;
import org.outsourcing.mhadminapi.service.UserService;
import org.outsourcing.mhadminapi.vo.Role;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    //@TODO: 유저 사용정지 topic send
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/users")
    public ResponseEntity<Page<UserDto.ReadResponse>> findUsers(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam int page,
            @RequestParam int size) {

        Page<UserDto.ReadResponse> response = userService.findUsers(
                gender, email, country, phoneNumber, start, end, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

//    @PreAuthorize("isAuthenticated()")
//    @GetMapping
//    public ResponseEntity<Page<UserDto.ReadResponse>> findAllUser(@RequestParam int page, @RequestParam int size) {
//
//        Page<UserDto.ReadResponse> response = userService.findAllUser(page, size);
//
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }
//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/gender")
//    public ResponseEntity<Page<UserDto.ReadResponse>> findUserByGender(@RequestParam String gender, @RequestParam int page, @RequestParam int size) {
//
//        Page<UserDto.ReadResponse> response = userService.findUserByGender(gender, page, size);
//
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }
//
//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/email")
//    public ResponseEntity<Page<UserDto.ReadResponse>> findUserByEmailContaining(@RequestParam String email, @RequestParam int page, @RequestParam int size) {
//
//        Page<UserDto.ReadResponse> response = userService.findUserByEmailContaining(email, page, size);
//
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }
//
//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/country")
//    public ResponseEntity<Page<UserDto.ReadResponse>> findUserByCountry(@RequestParam String country, @RequestParam int page, @RequestParam int size) {
//
//        Page<UserDto.ReadResponse> response = userService.findUserByCountryContaining(country, page, size);
//
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }
//
//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/phone-number")
//    public ResponseEntity<Page<UserDto.ReadResponse>> findUserByPhoneNumberContaining(@RequestParam String phoneNumber, @RequestParam int page, @RequestParam int size) {
//
//        Page<UserDto.ReadResponse> response = userService.findUserByPhoneNumberContaining(phoneNumber, page, size);
//
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }
//
//
//    //일자별 조회 ex) created_at : 2023.01.01 ~ 2024.01.01
//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/created-at")
//    public ResponseEntity<Page<UserDto.ReadResponse>> findUserByCreatedAtBetween(@RequestParam String start, @RequestParam String end, @RequestParam int page, @RequestParam int size) {
//
//        Page<UserDto.ReadResponse> response = userService.findUserByCreatedAtBetween(start, end, page, size);
//
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user-skin/created-at")
    public ResponseEntity<Page<UserDto.ReadUserSkinResponse>> findUserSkinByCreatedAtBetween(@RequestParam String start, @RequestParam String end, @RequestParam int page, @RequestParam int size) {

        Page<UserDto.ReadUserSkinResponse> response = userService.findUserSkinByCreatedAtBetween(start, end, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //회원별 등록한 here보기
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user-skin")
    public ResponseEntity<Page<UserDto.ReadUserSkinResponse>> findUserSkinByUserId(@RequestParam String userId, @RequestParam int page, @RequestParam int size) {

        Page<UserDto.ReadUserSkinResponse> response = userService.findUserSkinByUserId(userId, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all-user-skin")
    public ResponseEntity<Page<UserDto.ReadUserSkinResponse>> findAllUserSkin(@RequestParam int page, @RequestParam int size) {

        Page<UserDto.ReadUserSkinResponse> response = userService.findAllUserSkin(page, size);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAuthority('MASTER')")
    @DeleteMapping("/user-skin")
    public ResponseEntity<UserDto.DeleteUserSkinResponse> deleteUserSkin(@RequestBody UserDto.DeleteUserSkinRequest request) {

        UserDto.DeleteUserSkinResponse response = userService.deleteUserSkin(request.getUserSkinId());

        //@TODO : delete skin send message

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('MASTER')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<UserDto.DeleteResponse> deleteUser(@PathVariable String userId) {

        UserDto.DeleteResponse response = userService.deleteUser(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto.ReadResponse> findUserById(@PathVariable String userId) {

        UserDto.ReadResponse response = userService.findUserById(userId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }



    @PreAuthorize("hasAuthority('MASTER')")
    @PostMapping("/pause")
    public ResponseEntity<UserDto.PauseResponse> pauseUser(@RequestBody UserDto.PauseRequest request) {

        UserDto.PauseResponse response = userService.pauseUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
