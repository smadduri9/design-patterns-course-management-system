package edu.university.cms.application;

import edu.university.cms.domain.GradingStrategyType;
import edu.university.cms.domain.SubmissionType;

import java.time.LocalDate;
import java.util.Set;

public record CreateAssignmentRequest(
        String title,
        String description,
        LocalDate dueDate,
        Set<SubmissionType> acceptedSubmissionTypes,
        GradingStrategyType gradingStrategyType,
        int maxPoints,
        RubricRequest rubric
) {
}
