package edu.university.cms.application;

import java.util.UUID;

public record StudentFeedbackResponse(
        UUID submissionId,
        String finalFeedback,
        GradeResponse grade,
        String aiSummary,
        NotificationResponse notification,
        AIAnalysisReportResponse report
) {
}
