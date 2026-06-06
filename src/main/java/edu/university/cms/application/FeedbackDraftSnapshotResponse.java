package edu.university.cms.application;

import edu.university.cms.patterns.behavioral.memento.FeedbackDraftMemento;

import java.time.Instant;

public record FeedbackDraftSnapshotResponse(
        int index,
        String feedbackText,
        Instant savedAt
) {

    public static FeedbackDraftSnapshotResponse from(int index, FeedbackDraftMemento memento) {
        return new FeedbackDraftSnapshotResponse(index, memento.feedbackText(), memento.savedAt());
    }
}
