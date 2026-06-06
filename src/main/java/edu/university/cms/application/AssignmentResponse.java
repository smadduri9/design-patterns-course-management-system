package edu.university.cms.application;

import edu.university.cms.domain.Assignment;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record AssignmentResponse(
        UUID id,
        UUID courseId,
        String title,
        String description,
        LocalDate dueDate,
        List<String> acceptedSubmissionTypes,
        String gradingStrategyType,
        int maxPoints,
        RubricResponse rubric
) {

    public static AssignmentResponse from(Assignment assignment, UUID courseId) {
        return new AssignmentResponse(
                assignment.getId(),
                courseId,
                assignment.getTitle(),
                assignment.getDescription(),
                assignment.getDueDate(),
                assignment.getAcceptedSubmissionTypes().stream()
                        .map(Enum::name)
                        .sorted()
                        .toList(),
                assignment.getGradingStrategyType().name(),
                assignment.getMaxPoints(),
                RubricResponse.from(assignment.getRubric())
        );
    }
}
