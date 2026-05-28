package edu.university.cms.web;

import edu.university.cms.application.InstructorReviewFacade;
import edu.university.cms.application.InstructorReviewResult;
import edu.university.cms.application.PatternTraceEvent;
import edu.university.cms.application.PatternTraceService;
import edu.university.cms.application.StudentFeedbackService;
import edu.university.cms.application.StudentFeedbackView;
import edu.university.cms.domain.AIAnalysisReport;
import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.Grade;
import edu.university.cms.domain.GradingStrategyType;
import edu.university.cms.domain.Rubric;
import edu.university.cms.domain.RubricCriterion;
import edu.university.cms.domain.Submission;
import edu.university.cms.domain.SubmissionStatus;
import edu.university.cms.domain.SubmissionType;
import edu.university.cms.domain.User;
import edu.university.cms.domain.UserRole;
import edu.university.cms.patterns.behavioral.strategy.GradingStrategySelector;
import edu.university.cms.patterns.creational.builder.AssignmentBuilder;
import edu.university.cms.patterns.structural.adapter.MockAIService;
import edu.university.cms.patterns.structural.adapter.MockAIServiceAdapter;
import edu.university.cms.patterns.structural.adapter.MockCodeSandbox;
import edu.university.cms.patterns.structural.adapter.MockCodeSandboxAdapter;
import edu.university.cms.patterns.structural.proxy.DefaultAnalysisService;
import edu.university.cms.repository.SubmissionRepository;
import edu.university.cms.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
public class Phase5DemoController {

    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final InstructorReviewFacade instructorReviewFacade;
    private final StudentFeedbackService studentFeedbackService;
    private final PatternTraceService traceService;

    public Phase5DemoController(
            UserRepository userRepository,
            SubmissionRepository submissionRepository,
            InstructorReviewFacade instructorReviewFacade,
            StudentFeedbackService studentFeedbackService,
            PatternTraceService traceService
    ) {
        this.userRepository = userRepository;
        this.submissionRepository = submissionRepository;
        this.instructorReviewFacade = instructorReviewFacade;
        this.studentFeedbackService = studentFeedbackService;
        this.traceService = traceService;
    }

    @GetMapping("/demo/phase-5")
    public Phase5DemoResponse showPhase5Demo() {
        User student = userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.STUDENT)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Seeded student was not found"));
        Rubric rubric = new Rubric(
                UUID.randomUUID(),
                "Review Demo Rubric",
                List.of(new RubricCriterion(UUID.randomUUID(), "Completeness", "Complete final review flow.", 100))
        );
        Assignment assignment = new AssignmentBuilder()
                .title("Phase 5 Review Demo")
                .description("Demo assignment for review and notification.")
                .dueDate(LocalDate.now().plusDays(7))
                .acceptedSubmissionType(SubmissionType.PDF_TEXT)
                .rubric(rubric)
                .maxPoints(100)
                .gradingStrategyType(GradingStrategyType.RUBRIC_WEIGHTED)
                .build();
        Submission submission = new Submission(
                UUID.randomUUID(),
                assignment.getId(),
                student,
                SubmissionType.PDF_TEXT,
                "This submission is ready for instructor review.",
                Instant.now(),
                SubmissionStatus.ANALYZING
        );
        AIAnalysisReport report = new DefaultAnalysisService(
                traceService,
                new MockAIServiceAdapter(new MockAIService(), traceService),
                new MockCodeSandboxAdapter(new MockCodeSandbox(), traceService),
                new GradingStrategySelector()
        ).analyze(submission, assignment);
        submission.setReport(report);
        submission.markAwaitingReview(traceService);
        submissionRepository.save(submission);

        traceService.clear();
        InstructorReviewResult reviewResult = instructorReviewFacade.reviewAndFinalize(
                submission.getId(),
                "First instructor edit saved as a snapshot.",
                "Second instructor edit before restore.",
                true
        );
        StudentFeedbackView studentFeedback = studentFeedbackService.viewFeedback(submission.getId());

        return new Phase5DemoResponse(
                reviewResult.submission().getStatus().name(),
                reviewResult.finalFeedback(),
                gradeText(studentFeedback.grade()),
                studentFeedback.notification().getMessage(),
                studentFeedback.aiSummary(),
                traceService.findAll()
        );
    }

    private String gradeText(Grade grade) {
        return grade.points() + "/" + grade.maxPoints();
    }

    public record Phase5DemoResponse(
            String finalSubmissionStatus,
            String finalFeedback,
            String grade,
            String notificationMessage,
            String aiSummary,
            List<PatternTraceEvent> patternTraceEvents
    ) {
    }
}
