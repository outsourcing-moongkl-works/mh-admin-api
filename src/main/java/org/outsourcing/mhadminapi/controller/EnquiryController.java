package org.outsourcing.mhadminapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.auth.UserPrincipal;
import org.outsourcing.mhadminapi.dto.EnquiryDto;
import org.outsourcing.mhadminapi.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.outsourcing.mhadminapi.service.EnquiryService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/enquiry")
public class EnquiryController {

    private final EnquiryService enquiryService;

    @PreAuthorize("hasAuthority('MASTER')")
    @PostMapping("/reply")
    public ResponseEntity<EnquiryDto.ReplyResponse> replyEnquiry(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody EnquiryDto.ReplyRequest request) {
        log.info("replyEnquiry: {}", request);

        Admin admin = userPrincipal.getAdmin();

        log.info("admin: {}", admin.getEmail());

        EnquiryDto.ReplyResponse response = enquiryService.replyEnquiry(admin.getEmail(), request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<EnquiryDto.GetResponse>> getEnquiries(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                      @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<EnquiryDto.GetResponse> response = enquiryService.getEnquiries(page, size);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
