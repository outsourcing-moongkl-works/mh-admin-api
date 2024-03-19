package org.outsourcing.mhadminapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.dto.ResponseDto;
import org.outsourcing.mhadminapi.dto.SmtpDto;
import org.outsourcing.mhadminapi.service.SmtpService;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @Slf4j @RequiredArgsConstructor @RequestMapping("/smtp")
public class SmtpController {

    private final SmtpService smtpService;

    @PostMapping("/send")
    public ResponseEntity<ResponseDto> sendMail(SmtpDto.SendRequest request) {
        smtpService.sendEmail(request);
        return ResponseEntity.ok(ResponseDto.success("Mail sent"));
    }
}
