package edu.university.cms.application;

import edu.university.cms.domain.Notification;
import edu.university.cms.domain.Submission;

public record InstructorReviewResult(
        Submission submission,
        String restoredDraftText,
        String finalFeedback,
        Notification notification
) {
}
