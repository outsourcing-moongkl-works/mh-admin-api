package org.outsourcing.mhadminapi.service;

import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.dto.SmtpDto;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service @Slf4j
public class SmtpService {
    private final MailSender mailSender;

    public SmtpService(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(SmtpDto.SendRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("k2c2jung2@gmail.com");
        message.setTo(request.getTo());
        message.setSubject(request.getSubject());
        message.setText(request.getMessage());
        mailSender.send(message);
        log.info("Sending email to: " + request.getTo());
        log.info("Subject: " + request.getSubject());
        log.info("Message: " + request.getMessage());
    }
}
