package edu.university.cms.patterns.creational.builder;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.GradingStrategyType;
import edu.university.cms.domain.OfficialPattern;
import edu.university.cms.domain.Rubric;
import edu.university.cms.domain.SubmissionType;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class AssignmentBuilder {

    private final PatternTraceService traceService;
    private UUID id = UUID.randomUUID();
    private String title;
    private String description;
    private LocalDate dueDate = LocalDate.now().plusWeeks(1);
    private Set<SubmissionType> acceptedSubmissionTypes = EnumSet.noneOf(SubmissionType.class);
    private Rubric rubric;
    private Integer maxPoints;
    private GradingStrategyType gradingStrategyType = GradingStrategyType.RUBRIC_WEIGHTED;

    public AssignmentBuilder() {
        this(null);
    }

    public AssignmentBuilder(PatternTraceService traceService) {
        this.traceService = traceService;
    }

    public AssignmentBuilder id(UUID id) {
        this.id = Objects.requireNonNull(id, "id is required");
        return this;
    }

    public AssignmentBuilder title(String title) {
        this.title = title;
        return this;
    }

    public AssignmentBuilder description(String description) {
        this.description = description;
        return this;
    }

    public AssignmentBuilder dueDate(LocalDate dueDate) {
        this.dueDate = Objects.requireNonNull(dueDate, "dueDate is required");
        return this;
    }

    public AssignmentBuilder acceptedSubmissionType(SubmissionType type) {
        this.acceptedSubmissionTypes.add(Objects.requireNonNull(type, "type is required"));
        return this;
    }

    public AssignmentBuilder acceptedSubmissionTypes(Set<SubmissionType> types) {
        if (types == null || types.isEmpty()) {
            throw new IllegalArgumentException("at least one submission type is required");
        }
        this.acceptedSubmissionTypes = EnumSet.copyOf(types);
        return this;
    }

    public AssignmentBuilder rubric(Rubric rubric) {
        this.rubric = rubric;
        return this;
    }

    public AssignmentBuilder maxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
        return this;
    }

    public AssignmentBuilder gradingStrategyType(GradingStrategyType gradingStrategyType) {
        this.gradingStrategyType = Objects.requireNonNull(gradingStrategyType, "gradingStrategyType is required");
        return this;
    }

    public Assignment build() {
        validate();
        Assignment assignment = new Assignment(
                id,
                title,
                description,
                dueDate,
                acceptedSubmissionTypes,
                rubric,
                maxPoints,
                gradingStrategyType
        );
        if (traceService != null) {
            traceService.recordPhase2(
                    OfficialPattern.BUILDER,
                    getClass().getSimpleName(),
                    "Create assignment",
                    "Assignment was assembled step by step and validated before creation",
                    "Instructor creates assignment"
            );
        }
        return assignment;
    }

    private void validate() {
        requireText(title, "title is required");
        requireText(description, "description is required");
        if (acceptedSubmissionTypes.isEmpty()) {
            throw new IllegalStateException("at least one submission type is required");
        }
        if (rubric == null) {
            throw new IllegalStateException("rubric is required");
        }
        if (maxPoints == null || maxPoints <= 0) {
            throw new IllegalStateException("maxPoints must be positive");
        }
    }

    private static void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(message);
        }
    }
}
