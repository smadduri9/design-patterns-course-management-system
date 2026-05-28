package edu.university.cms.application;

import edu.university.cms.domain.SubmissionType;

import java.util.UUID;

public record SubmissionRequest(
        UUID studentId,
        UUID assignmentId,
        SubmissionType submissionType,
        String content
) {
}
