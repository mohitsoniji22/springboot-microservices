package com.example.ns.api.service;

import com.example.ns.api.domain.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.mail.*;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    public void send(NotificationMessage message) {

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(message.getEmail());
        mail.setSubject(message.getSubject());
        mail.setText(message.getBody());

        mailSender.send(mail);
        log.info("Email sent to user {}", message.getUsername());
    }
}
