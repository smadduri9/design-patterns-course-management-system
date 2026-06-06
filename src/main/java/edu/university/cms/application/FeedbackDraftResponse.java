package edu.university.cms.application;

import java.util.List;
import java.util.UUID;

public record FeedbackDraftResponse(
        UUID submissionId,
        String currentFeedback,
        List<FeedbackDraftSnapshotResponse> drafts
) {
}
