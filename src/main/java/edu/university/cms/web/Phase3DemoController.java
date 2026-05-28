package edu.university.cms.web;

import edu.university.cms.application.PatternTraceEvent;
import edu.university.cms.application.PatternTraceService;
import edu.university.cms.application.SubmissionWorkflowFacade;
import edu.university.cms.application.SubmissionWorkflowResult;
import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.Rubric;
import edu.university.cms.domain.RubricCriterion;
import edu.university.cms.domain.SubmissionType;
import edu.university.cms.domain.User;
import edu.university.cms.domain.UserRole;
import edu.university.cms.patterns.creational.builder.AssignmentBuilder;
import edu.university.cms.repository.AssignmentRepository;
import edu.university.cms.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
public class Phase3DemoController {

    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionWorkflowFacade submissionWorkflowFacade;
    private final PatternTraceService traceService;

    public Phase3DemoController(
            UserRepository userRepository,
            AssignmentRepository assignmentRepository,
            SubmissionWorkflowFacade submissionWorkflowFacade,
            PatternTraceService traceService
    ) {
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionWorkflowFacade = submissionWorkflowFacade;
        this.traceService = traceService;
    }

    @GetMapping("/demo/phase-3")
    public Phase3DemoResponse showPhase3Demo() {
        User student = userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.STUDENT)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Seeded student was not found"));

        Rubric rubric = new Rubric(
                UUID.randomUUID(),
                "Submission Workflow Rubric",
                List.of(new RubricCriterion(
                        UUID.randomUUID(),
                        "Completeness",
                        "Submission includes enough content for placeholder analysis.",
                        100
                ))
        );
        Assignment textAssignment = assignmentRepository.save(new AssignmentBuilder()
                .title("Text Submission Demo")
                .description("Submit PDF/text content for workflow validation.")
                .dueDate(LocalDate.now().plusDays(7))
                .acceptedSubmissionType(SubmissionType.PDF_TEXT)
                .rubric(rubric)
                .maxPoints(100)
                .build());
        Assignment codeAssignment = assignmentRepository.save(new AssignmentBuilder()
                .title("Java Code Submission Demo")
                .description("Submit Java code content for workflow validation.")
                .dueDate(LocalDate.now().plusDays(7))
                .acceptedSubmissionType(SubmissionType.JAVA_CODE)
                .rubric(rubric)
                .maxPoints(100)
                .build());

        traceService.clear();
        SubmissionWorkflowResult textResult = submissionWorkflowFacade.submitAssignment(
                student.getId(),
                textAssignment.getId(),
                SubmissionType.PDF_TEXT,
                "This PDF/text submission explains how a design pattern improves course workflow clarity."
        );
        SubmissionWorkflowResult codeResult = submissionWorkflowFacade.submitAssignment(
                student.getId(),
                codeAssignment.getId(),
                SubmissionType.JAVA_CODE,
                "public class PatternDemo { public String explain() { return \"Phase 3\"; } }"
        );

        return new Phase3DemoResponse(
                student.getName(),
                new DemoSubmission(
                        textAssignment.getTitle(),
                        textResult.submission().getStatus().name(),
                        textResult.submission().getType().name(),
                        textResult.analysisReport().getSummary()
                ),
                new DemoSubmission(
                        codeAssignment.getTitle(),
                        codeResult.submission().getStatus().name(),
                        codeResult.submission().getType().name(),
                        codeResult.analysisReport().getSummary()
                ),
                traceService.findAll()
        );
    }

    public record Phase3DemoResponse(
            String student,
            DemoSubmission textSubmission,
            DemoSubmission codeSubmission,
            List<PatternTraceEvent> patternTraceEvents
    ) {
    }

    public record DemoSubmission(
            String assignment,
            String finalStatus,
            String submissionType,
            String analysisSummary
    ) {
    }
}
