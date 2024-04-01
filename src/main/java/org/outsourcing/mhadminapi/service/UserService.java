package org.outsourcing.mhadminapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.dto.MessageDto;
import org.outsourcing.mhadminapi.dto.UserDto;
import org.outsourcing.mhadminapi.entity.User;
import org.outsourcing.mhadminapi.entity.UserSkin;
import org.outsourcing.mhadminapi.repository.UserSkinRepository;
import org.outsourcing.mhadminapi.repository.UserRepository;
import org.outsourcing.mhadminapi.sqs.SqsSender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service @Slf4j @RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserSkinRepository userSkinRepository;
    private final StringRedisTemplate redisTemplate;
    private final SqsSender sqsSender;
    //findUserByGender
//    public Page<UserDto.ReadResponse> findUserByGender(String gender, int page, int size) {
//        final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
//
//        return userRepository.findUserByGender(gender, pageable);
//    }
//
//    //findUserByEmailContaining
//    public Page<UserDto.ReadResponse> findUserByEmailContaining(String email, int page, int size) {
//        final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
//
//        return userRepository.findUserByEmailContaining(email, pageable);
//    }
//
//    //findUserByCountryContaining
//    public Page<UserDto.ReadResponse> findUserByCountryContaining(String country, int page, int size) {
//        final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
//
//        return userRepository.findUserByCountryContaining(country, pageable);
//    }
//
//    //findUserByPhoneNumberContaining
//    public Page<UserDto.ReadResponse> findUserByPhoneNumberContaining(String phoneNumber, int page, int size) {
//        final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
//
//        return userRepository.findUserByPhoneNumberContaining(phoneNumber, pageable);
//    }
//    //findAllUser
//    public Page<UserDto.ReadResponse> findAllUser(int page, int size) {
//        final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
//
//        return userRepository.findAllUser(pageable);
//    }
//
//    public Page<UserDto.ReadResponse> findUserByCreatedAtBetween(String start, String end, int page, int size) {
//        final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDate startDate = LocalDate.parse(start, formatter);
//        LocalDate endDate = LocalDate.parse(end, formatter);
//
//        // JPA 레포지토리 메서드 호출 시 LocalDate 객체를 전달
//        return userRepository.findUserByCreatedAtBetween(startDate, endDate, pageable);
//    }
    public Page<UserDto.ReadResponse> findUsers(String gender, String email, String country, String phoneNumber, String start, String end, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        // 날짜 범위 파싱
        if (start != null && !start.isEmpty() && end != null && !end.isEmpty()) {
            startDateTime = LocalDate.parse(start).atStartOfDay();
            endDateTime = LocalDate.parse(end).atTime(23, 59, 59);
        }

        // 조건에 따른 분기 처리
        if (gender != null) {
            return userRepository.findUserByGender(gender, pageable);
        } else if (email != null) {
            return userRepository.findUserByEmailContaining(email, pageable);
        } else if (country != null) {
            return userRepository.findUserByCountryContaining(country, pageable);
        } else if (phoneNumber != null) {
            return userRepository.findUserByPhoneNumberContaining(phoneNumber, pageable);
        } else if (startDateTime != null && endDateTime != null) {
            return userRepository.findUserByCreatedAtBetween(startDateTime, endDateTime, pageable);
        } else {
            return userRepository.findAllUser(pageable);
        }
    }


    //findUserById
    public UserDto.ReadResponse findUserById(String userId) {

        UserDto.ReadResponse response = userRepository.findUserById(UUID.fromString(userId)).orElse(null);

        if(response == null) {
            throw new NoSuchElementException("User not found");
        }

        return response;
    }


    public Page<UserDto.ReadUserSkinResponse> findUserSkinByUserId(String userId, int page, int size) {
        final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return userSkinRepository.findUserSkinByUserId(UUID.fromString(userId), pageable);
    }


    public Page<UserDto.ReadUserSkinResponse> findAllUserSkin(int page, int size) {
        final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return userSkinRepository.findAllUserSkin(pageable);
    }

    public Page<UserDto.ReadUserSkinResponse> findUserSkinByCreatedAtBetween(String start, String end, int page, int size) {
        final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(start, formatter);
        LocalDate endDate = LocalDate.parse(end, formatter);

        return userSkinRepository.findUserSkinByCreatedAtBetween(startDate, endDate, pageable);
    }
    @Transactional
    public UserDto.DeleteUserPostResponse deleteUserPost(UserDto.DeleteUserPostRequest request) {
        UserSkin userSkin = userSkinRepository.findById(UUID.fromString(request.getPostId())).orElseThrow(() -> new NoSuchElementException("UserPost not found"));

        userSkinRepository.delete(userSkin);

        Map<String, String> messageMap = new LinkedHashMap<>();

        messageMap.put("postId", request.getPostId());
        messageMap.put("userId", request.getUserId());

        MessageDto messageDto = sqsSender.createMessageDtoFromRequest("delete user post", messageMap);

        sqsSender.sendToSQS(messageDto);

        return UserDto.DeleteUserPostResponse.builder().deletedAt(LocalDateTime.now()).build();
    }


    @Transactional
    public UserDto.DeleteResponse deleteUser(UserDto.DeleteUserRequest request) {
        User user = userRepository.findById(UUID.fromString(request.getUserId())).orElseThrow(() -> new NoSuchElementException("User not found"));
        userRepository.delete(user);

        Map<String, String> messageMap = new LinkedHashMap<>();

        messageMap.put("userId", request.getUserId());

        MessageDto messageDto = sqsSender.createMessageDtoFromRequest("delete user", messageMap);

        sqsSender.sendToSQS(messageDto);
        
        return UserDto.DeleteResponse.builder().deletedAt(LocalDateTime.now()).build();
    }



}
