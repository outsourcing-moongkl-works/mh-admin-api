package org.outsourcing.mhadminapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.dto.EnquiryDto;
import org.outsourcing.mhadminapi.dto.MailDto;
import org.outsourcing.mhadminapi.repository.EnquiryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service @Slf4j @RequiredArgsConstructor
public class EnquiryService {

    private final EnquiryRepository enquiryRepository;
    private final MailService mailService;
    //getEnquiries
    public Page<EnquiryDto.GetResponse> getEnquiries(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<EnquiryDto.GetResponse> response = enquiryRepository.findAllByOrderByCreatedAtDesc(pageable);

        return response;
    }


    public EnquiryDto.ReplyResponse replyEnquiry(String Email, EnquiryDto.ReplyRequest request) {
        MailDto.MailSendDto mailSendDto = MailDto.MailSendDto.builder()
                .to(request.getEmail())
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        mailService.sendMail(mailSendDto);

        EnquiryDto.ReplyResponse response = EnquiryDto.ReplyResponse.builder()
                .email(request.getEmail())
                .Email(Email)
                .createdAt(LocalDateTime.now())
                .build();
        return response;
    }
}
