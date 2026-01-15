package com.example.ns.api.domain;

import lombok.*;

@Data
@Builder
public class NotificationMessage {

    private String username;
    private String email;
    private String subject;
    private String body;
    private NotificationType type;
}