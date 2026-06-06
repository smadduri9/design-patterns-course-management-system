package edu.university.cms.application;

import edu.university.cms.domain.Submission;

import java.util.UUID;

public record AnalysisResponse(
        UUID submissionId,
        String status,
        AIAnalysisReportResponse report
) {

    public static AnalysisResponse from(Submission submission) {
        return new AnalysisResponse(
                submission.getId(),
                submission.getStatus().name(),
                submission.getReport()
                        .map(AIAnalysisReportResponse::from)
                        .orElse(null)
        );
    }
}
