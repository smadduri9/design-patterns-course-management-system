package edu.university.cms.patterns.behavioral.chain;

import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.SubmissionType;
import edu.university.cms.domain.User;

public record SubmissionValidationContext(
        User student,
        Assignment assignment,
        SubmissionType submissionType,
        String content
) {
}
