package edu.university.cms.application;

import java.util.UUID;

public record FinalFeedbackResponse(
        UUID submissionId,
        String status,
        String finalFeedback,
        GradeResponse grade,
        NotificationResponse notification
) {
}
