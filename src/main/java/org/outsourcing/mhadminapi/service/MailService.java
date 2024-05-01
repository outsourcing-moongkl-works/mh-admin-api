package org.outsourcing.mhadminapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.dto.MailDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service @Slf4j
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String FROM_ADDRESS;

    public void sendMail(MailDto.MailSendDto mailSendDto) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(mailSendDto.getTo());
        mailMessage.setFrom(FROM_ADDRESS);
        mailMessage.setSubject(mailSendDto.getTitle());
        mailMessage.setText(mailSendDto.getContent());

        mailSender.send(mailMessage);
    }
}
