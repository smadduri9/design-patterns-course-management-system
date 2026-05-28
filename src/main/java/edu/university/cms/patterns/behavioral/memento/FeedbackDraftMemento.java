package edu.university.cms.patterns.behavioral.memento;

import java.time.Instant;

public record FeedbackDraftMemento(String feedbackText, Instant savedAt) {
}
