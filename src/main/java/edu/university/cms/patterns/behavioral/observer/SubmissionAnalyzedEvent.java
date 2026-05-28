package edu.university.cms.patterns.behavioral.observer;

import java.time.Instant;
import java.util.UUID;

public record SubmissionAnalyzedEvent(UUID submissionId, Instant occurredAt) implements DomainEvent {
}
