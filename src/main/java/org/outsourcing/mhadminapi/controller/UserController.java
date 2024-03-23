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
    @GetMapping
    public ResponseEntity<Page<UserDto.ReadResponse>> findAllUser(@RequestParam int page, @RequestParam int size) {

        Page<UserDto.ReadResponse> response = userService.findAllUser(page, size);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto.ReadResponse> findUserById(@PathVariable String userId) {

        UserDto.ReadResponse response = userService.findUserById(userId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAuthority('MASTER')")
    @PostMapping("/{userId}")
    public ResponseEntity<UserDto.DeleteResponse> deleteUser(@PathVariable String userId) {

        UserDto.DeleteResponse response = userService.deleteUser(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/gender")
    public ResponseEntity<Page<UserDto.ReadResponse>> findUserByGender(@RequestParam String gender, @RequestParam int page, @RequestParam int size) {

        Page<UserDto.ReadResponse> response = userService.findUserByGender(gender, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/email")
    public ResponseEntity<Page<UserDto.ReadResponse>> findUserByEmailContaining(@RequestParam String email, @RequestParam int page, @RequestParam int size) {

        Page<UserDto.ReadResponse> response = userService.findUserByEmailContaining(email, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/country")
    public ResponseEntity<Page<UserDto.ReadResponse>> findUserByCountry(@RequestParam String country, @RequestParam int page, @RequestParam int size) {

        Page<UserDto.ReadResponse> response = userService.findUserByCountryContaining(country, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/phone-number")
    public ResponseEntity<Page<UserDto.ReadResponse>> findUserByPhoneNumberContaining(@RequestParam String phoneNumber, @RequestParam int page, @RequestParam int size) {

        Page<UserDto.ReadResponse> response = userService.findUserByPhoneNumberContaining(phoneNumber, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    //일자별 조회 ex) created_at : 2023.01.01 ~ 2024.01.01
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/created-at")
    public ResponseEntity<Page<UserDto.ReadResponse>> findUserByCreatedAtBetween(@RequestParam String start, @RequestParam String end, @RequestParam int page, @RequestParam int size) {

        Page<UserDto.ReadResponse> response = userService.findUserByCreatedAtBetween(start, end, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/here/created-at")
    public ResponseEntity<Page<UserDto.ReadHereResponse>> findHereByCreatedAtBetween(@RequestParam String start, @RequestParam String end, @RequestParam int page, @RequestParam int size) {

        Page<UserDto.ReadHereResponse> response = userService.findHereByCreatedAtBetween(start, end, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //회원별 등록한 here보기
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/here/{userId}")
    public ResponseEntity<Page<UserDto.ReadHereResponse>> findHereByUserId(@PathVariable String userId, @RequestParam int page, @RequestParam int size) {

        Page<UserDto.ReadHereResponse> response = userService.findHereByUserId(userId, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/here")
    public ResponseEntity<Page<UserDto.ReadHereResponse>> findAllHere(@RequestParam int page, @RequestParam int size) {

        Page<UserDto.ReadHereResponse> response = userService.findAllHere(page, size);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/here/{hereId}")
    public ResponseEntity<UserDto.ReadHereResponse> findHereById(@PathVariable String hereId) {

        UserDto.ReadHereResponse response = userService.findHereById(hereId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAuthority('MASTER')")
    @DeleteMapping("/here/{hereId}")
    public ResponseEntity<UserDto.DeleteHereResponse> deleteHere(@PathVariable String hereId) {

        UserDto.DeleteHereResponse response = userService.deleteHere(hereId);

        //@TODO : delete here send message

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
