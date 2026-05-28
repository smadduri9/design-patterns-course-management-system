package edu.university.cms.application;

import edu.university.cms.domain.Submission;

import java.time.Instant;
import java.util.UUID;

public record SubmissionListItemResponse(
        UUID id,
        UUID assignmentId,
        UserResponse student,
        String type,
        String status,
        Instant submittedAt,
        boolean hasAnalysisReport
) {

    public static SubmissionListItemResponse from(Submission submission) {
        return new SubmissionListItemResponse(
                submission.getId(),
                submission.getAssignmentId(),
                UserResponse.from(submission.getStudent()),
                submission.getType().name(),
                submission.getStatus().name(),
                submission.getSubmittedAt(),
                submission.getReport().isPresent()
        );
    }
}
