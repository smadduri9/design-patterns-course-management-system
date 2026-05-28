package edu.university.cms.domain;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Assignment {

    private final UUID id;
    private final String title;
    private final String description;
    private final LocalDate dueDate;
    private final Set<SubmissionType> acceptedSubmissionTypes;
    private final Rubric rubric;
    private final int maxPoints;
    private final GradingStrategyType gradingStrategyType;

    public Assignment(
            UUID id,
            String title,
            String description,
            LocalDate dueDate,
            Set<SubmissionType> acceptedSubmissionTypes,
            Rubric rubric,
            int maxPoints
    ) {
        this(id, title, description, dueDate, acceptedSubmissionTypes, rubric, maxPoints, GradingStrategyType.RUBRIC_WEIGHTED);
    }

    public Assignment(
            UUID id,
            String title,
            String description,
            LocalDate dueDate,
            Set<SubmissionType> acceptedSubmissionTypes,
            Rubric rubric,
            int maxPoints,
            GradingStrategyType gradingStrategyType
    ) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.title = requireText(title, "title is required");
        this.description = requireText(description, "description is required");
        this.dueDate = Objects.requireNonNull(dueDate, "dueDate is required");
        if (acceptedSubmissionTypes == null || acceptedSubmissionTypes.isEmpty()) {
            throw new IllegalArgumentException("at least one submission type is required");
        }
        this.acceptedSubmissionTypes = EnumSet.copyOf(acceptedSubmissionTypes);
        this.rubric = Objects.requireNonNull(rubric, "rubric is required");
        if (maxPoints <= 0) {
            throw new IllegalArgumentException("maxPoints must be positive");
        }
        this.maxPoints = maxPoints;
        this.gradingStrategyType = Objects.requireNonNull(gradingStrategyType, "gradingStrategyType is required");
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Set<SubmissionType> getAcceptedSubmissionTypes() {
        return EnumSet.copyOf(acceptedSubmissionTypes);
    }

    public Rubric getRubric() {
        return rubric;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public GradingStrategyType getGradingStrategyType() {
        return gradingStrategyType;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
