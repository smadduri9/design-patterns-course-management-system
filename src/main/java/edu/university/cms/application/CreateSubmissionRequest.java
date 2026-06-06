package edu.university.cms.application;

import edu.university.cms.domain.SubmissionType;

import java.util.UUID;

public record CreateSubmissionRequest(
        UUID studentId,
        SubmissionType submissionType,
        String content
) {
}
