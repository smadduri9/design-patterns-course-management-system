package edu.university.cms.application;

import edu.university.cms.domain.Notification;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String message,
        Instant createdAt,
        boolean read
) {

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getMessage(),
                notification.getCreatedAt(),
                notification.isRead()
        );
    }
}
