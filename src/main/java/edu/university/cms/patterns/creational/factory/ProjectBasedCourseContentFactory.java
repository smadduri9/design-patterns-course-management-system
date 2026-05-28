package edu.university.cms.patterns.creational.factory;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.CourseModule;
import edu.university.cms.domain.OfficialPattern;
import edu.university.cms.domain.Rubric;
import edu.university.cms.domain.RubricCriterion;
import edu.university.cms.domain.SubmissionType;
import edu.university.cms.patterns.creational.builder.AssignmentBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ProjectBasedCourseContentFactory implements CourseContentFactory {

    private final PatternTraceService traceService;

    public ProjectBasedCourseContentFactory() {
        this(null);
    }

    public ProjectBasedCourseContentFactory(PatternTraceService traceService) {
        this.traceService = traceService;
    }

    @Override
    public CourseModule createStarterModule() {
        trace("Created a project-based starter module template");
        return new CourseModule(UUID.randomUUID(), "Module 1: Course Project Setup", List.of());
    }

    @Override
    public Rubric createStarterRubric() {
        trace("Created a project-based rubric template");
        return new Rubric(
                UUID.randomUUID(),
                "Project Assignment Rubric",
                List.of(
                        new RubricCriterion(UUID.randomUUID(), "Object-Oriented Design", "Uses clear responsibilities and collaboration.", 35),
                        new RubricCriterion(UUID.randomUUID(), "Pattern Fit", "Applies required patterns in justified locations.", 45),
                        new RubricCriterion(UUID.randomUUID(), "Code Quality", "Keeps implementation simple and readable.", 20)
                )
        );
    }

    @Override
    public Assignment createStarterAssignment(Rubric rubric) {
        trace("Created a project assignment template compatible with the project rubric");
        return new AssignmentBuilder(traceService)
                .title("Design Patterns Project Milestone")
                .description("Submit Java code for the first project milestone.")
                .dueDate(LocalDate.now().plusWeeks(2))
                .acceptedSubmissionType(SubmissionType.JAVA_CODE)
                .rubric(rubric)
                .maxPoints(100)
                .build();
    }

    private void trace(String description) {
        if (traceService != null) {
            traceService.recordPhase2(
                    OfficialPattern.ABSTRACT_FACTORY,
                    getClass().getSimpleName(),
                    "Create project-based course content",
                    description,
                    "Instructor starts a project-based course"
            );
        }
    }
}
