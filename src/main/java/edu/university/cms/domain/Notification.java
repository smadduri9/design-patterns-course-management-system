package edu.university.cms.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Notification {

    private final UUID id;
    private final User recipient;
    private final String message;
    private final Instant createdAt;
    private boolean read;

    public Notification(UUID id, User recipient, String message, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.recipient = Objects.requireNonNull(recipient, "recipient is required");
        this.message = requireText(message, "message is required");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
    }

    public UUID getId() {
        return id;
    }

    public User getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public void markRead() {
        this.read = true;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
