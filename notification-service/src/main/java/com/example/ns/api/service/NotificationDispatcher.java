package com.example.ns.api.service;

import com.example.ns.api.domain.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDispatcher {

    private final EmailNotificationService emailService;

    public void dispatch(NotificationMessage message) {
        if (message.getType() == NotificationType.EMAIL) {
            emailService.send(message);
        }
        log.info("Notification dispatched to user {}", message.getUserId());
    }
}