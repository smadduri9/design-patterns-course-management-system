package edu.university.cms.application;

import edu.university.cms.domain.Submission;

import java.time.Instant;
import java.util.UUID;

public record SubmissionDetailResponse(
        UUID id,
        UUID assignmentId,
        UserResponse student,
        String type,
        String status,
        Instant submittedAt,
        boolean hasAnalysisReport,
        AIAnalysisReportResponse report
) {

    public static SubmissionDetailResponse from(Submission submission) {
        return new SubmissionDetailResponse(
                submission.getId(),
                submission.getAssignmentId(),
                UserResponse.from(submission.getStudent()),
                submission.getType().name(),
                submission.getStatus().name(),
                submission.getSubmittedAt(),
                submission.getReport().isPresent(),
                submission.getReport()
                        .map(AIAnalysisReportResponse::from)
                        .orElse(null)
        );
    }
}
