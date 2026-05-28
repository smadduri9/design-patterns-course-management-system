package edu.university.cms.application;

import edu.university.cms.domain.AIAnalysisReport;
import edu.university.cms.domain.Grade;
import edu.university.cms.domain.Notification;

public record StudentFeedbackView(
        String finalFeedback,
        Grade grade,
        String aiSummary,
        Notification notification,
        AIAnalysisReport report
) {
}
