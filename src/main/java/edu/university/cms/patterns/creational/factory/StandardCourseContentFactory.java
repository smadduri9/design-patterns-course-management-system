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

public class StandardCourseContentFactory implements CourseContentFactory {

    private final PatternTraceService traceService;

    public StandardCourseContentFactory() {
        this(null);
    }

    public StandardCourseContentFactory(PatternTraceService traceService) {
        this.traceService = traceService;
    }

    @Override
    public CourseModule createStarterModule() {
        trace("Created a standard starter module template");
        return new CourseModule(UUID.randomUUID(), "Module 1: Pattern Foundations", List.of());
    }

    @Override
    public Rubric createStarterRubric() {
        trace("Created a standard rubric template");
        return new Rubric(
                UUID.randomUUID(),
                "Standard Assignment Rubric",
                List.of(
                        new RubricCriterion(UUID.randomUUID(), "Concept Accuracy", "Explains the design pattern correctly.", 40),
                        new RubricCriterion(UUID.randomUUID(), "Application", "Connects the pattern to the assignment scenario.", 40),
                        new RubricCriterion(UUID.randomUUID(), "Clarity", "Communicates ideas clearly.", 20)
                )
        );
    }

    @Override
    public Assignment createStarterAssignment(Rubric rubric) {
        trace("Created a standard assignment template compatible with the standard rubric");
        return new AssignmentBuilder(traceService)
                .title("Design Pattern Reflection")
                .description("Submit a PDF or text explanation of one assigned design pattern.")
                .dueDate(LocalDate.now().plusWeeks(1))
                .acceptedSubmissionType(SubmissionType.PDF_TEXT)
                .rubric(rubric)
                .maxPoints(100)
                .build();
    }

    private void trace(String description) {
        if (traceService != null) {
            traceService.recordPhase2(
                    OfficialPattern.ABSTRACT_FACTORY,
                    getClass().getSimpleName(),
                    "Create standard course content",
                    description,
                    "Instructor starts a standard course"
            );
        }
    }
}
