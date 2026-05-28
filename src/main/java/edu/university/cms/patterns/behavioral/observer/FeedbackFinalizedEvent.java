package edu.university.cms.patterns.behavioral.observer;

import edu.university.cms.domain.Grade;
import edu.university.cms.domain.User;

import java.time.Instant;
import java.util.UUID;

public record FeedbackFinalizedEvent(
        UUID submissionId,
        User student,
        String finalFeedback,
        Grade grade,
        Instant occurredAt
) implements DomainEvent {
}
