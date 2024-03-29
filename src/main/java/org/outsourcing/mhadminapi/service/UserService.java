package org.outsourcing.mhadminapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.dto.UserDto;
import org.outsourcing.mhadminapi.entity.User;
import org.outsourcing.mhadminapi.entity.UserHere;
import org.outsourcing.mhadminapi.repository.UserHereRepository;
import org.outsourcing.mhadminapi.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service @Slf4j @RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserHereRepository userHereRepository;

    //findUserByGender
    public Page<UserDto.ReadResponse> findUserByGender(String gender, int page, int size) {
        final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return userRepository.findUserByGender(gender, pageable);
    }

    //findUserByEmailContaining
    public Page<UserDto.ReadResponse> findUserByEmailContaining(String email, int page, int size) {
        final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return userRepository.findUserByEmailContaining(email, pageable);
    }

    //findUserByCountryContaining
    public Page<UserDto.ReadResponse> findUserByCountryContaining(String country, int page, int size) {
        final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return userRepository.findUserByCountryContaining(country, pageable);
    }

    //findUserByPhoneNumberContaining
    public Page<UserDto.ReadResponse> findUserByPhoneNumberContaining(String phoneNumber, int page, int size) {
        final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return userRepository.findUserByPhoneNumberContaining(phoneNumber, pageable);
    }

    //deleteUser
    public UserDto.DeleteResponse deleteUser(String userId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new NoSuchElementException("User not found"));
        userRepository.delete(user);
        return UserDto.DeleteResponse.builder().deletedAt(LocalDateTime.now()).build();
    }
    //findAllUser
    public Page<UserDto.ReadResponse> findAllUser(int page, int size) {
        final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return userRepository.findAllUser(pageable);
    }

    //findUserById
    public UserDto.ReadResponse findUserById(String userId) {

        UserDto.ReadResponse response = userRepository.findUserById(UUID.fromString(userId)).orElse(null);

        if(response == null) {
            throw new NoSuchElementException("User not found");
        }

        return response;
    }

    public Page<UserDto.ReadResponse> findUserByCreatedAtBetween(String start, String end, int page, int size) {
        final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(start, formatter);
        LocalDate endDate = LocalDate.parse(end, formatter);

        // JPA 레포지토리 메서드 호출 시 LocalDate 객체를 전달
        return userRepository.findUserByCreatedAtBetween(startDate, endDate, pageable);
    }

    public Page<UserDto.ReadHereResponse> findHereByUserId(String userId, int page, int size) {
        final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return userHereRepository.findUserHereByUserId(UUID.fromString(userId), pageable);
    }


    public Page<UserDto.ReadHereResponse> findAllHere(int page, int size) {
        final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return userHereRepository.findAllHere(pageable);
    }

    public Page<UserDto.ReadHereResponse> findHereByCreatedAtBetween(String start, String end, int page, int size) {
        final Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(start, formatter);
        LocalDate endDate = LocalDate.parse(end, formatter);

        return userHereRepository.findHereByCreatedAtBetween(startDate, endDate, pageable);
    }

    public UserDto.ReadHereResponse findHereById(String hereId) {
        UserDto.ReadHereResponse response = userHereRepository.findHereById(UUID.fromString(hereId));

        if(response == null) {
            throw new NoSuchElementException("Here not found");
        }

        return response;
    }

    public UserDto.DeleteHereResponse deleteHere(String hereId) {
        UserHere userHere = userHereRepository.findById(UUID.fromString(hereId)).orElseThrow(() -> new NoSuchElementException("Here not found"));

        userHereRepository.delete(userHere);

        return UserDto.DeleteHereResponse.builder().deletedAt(LocalDateTime.now()).build();
    }

    public UserDto.PauseResponse pauseUser(UserDto.PauseRequest request) {
        return null;
    }
}
