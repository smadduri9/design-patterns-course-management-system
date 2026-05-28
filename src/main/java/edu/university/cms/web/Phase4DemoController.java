package edu.university.cms.web;

import edu.university.cms.application.PatternTraceEvent;
import edu.university.cms.application.PatternTraceService;
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
import edu.university.cms.patterns.structural.proxy.CachedAnalysisServiceProxy;
import edu.university.cms.patterns.structural.proxy.DefaultAnalysisService;
import edu.university.cms.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
public class Phase4DemoController {

    private final UserRepository userRepository;
    private final PatternTraceService traceService;

    public Phase4DemoController(UserRepository userRepository, PatternTraceService traceService) {
        this.userRepository = userRepository;
        this.traceService = traceService;
    }

    @GetMapping("/demo/phase-4")
    public Phase4DemoResponse showPhase4Demo() {
        User student = userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.STUDENT)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Seeded student was not found"));

        Rubric rubric = rubric();
        Assignment textAssignment = assignment("AI Text Analysis Demo", SubmissionType.PDF_TEXT, GradingStrategyType.RUBRIC_WEIGHTED, rubric);
        Assignment codeAssignment = assignment("AI Code Analysis Demo", SubmissionType.JAVA_CODE, GradingStrategyType.CODE_TEST, rubric);
        Submission textSubmission = submission(student, textAssignment, SubmissionType.PDF_TEXT,
                "This design patterns essay explains why adapters protect the domain model from mock service APIs.");
        Submission codeSubmission = submission(student, codeAssignment, SubmissionType.JAVA_CODE,
                "public class Demo { public String explain() { return \"adapter and proxy\"; } }");

        traceService.clear();
        CachedAnalysisServiceProxy analysisService = new CachedAnalysisServiceProxy(
                new DefaultAnalysisService(
                        traceService,
                        new MockAIServiceAdapter(new MockAIService(), traceService),
                        new MockCodeSandboxAdapter(new MockCodeSandbox(), traceService),
                        new GradingStrategySelector()
                ),
                traceService
        );

        AIAnalysisReport textReport = analysisService.analyze(textSubmission, textAssignment);
        AIAnalysisReport codeReport = analysisService.analyze(codeSubmission, codeAssignment);
        AIAnalysisReport cachedCodeReport = analysisService.analyze(codeSubmission, codeAssignment);

        return new Phase4DemoResponse(
                student.getName(),
                reportSummary(textAssignment, textReport),
                reportSummary(codeAssignment, codeReport),
                codeReport == cachedCodeReport,
                traceService.findAll()
        );
    }

    private Assignment assignment(
            String title,
            SubmissionType type,
            GradingStrategyType gradingStrategyType,
            Rubric rubric
    ) {
        return new AssignmentBuilder()
                .title(title)
                .description("Phase 4 analysis demo assignment.")
                .dueDate(LocalDate.now().plusDays(7))
                .acceptedSubmissionType(type)
                .rubric(rubric)
                .maxPoints(100)
                .gradingStrategyType(gradingStrategyType)
                .build();
    }

    private Rubric rubric() {
        return new Rubric(
                UUID.randomUUID(),
                "AI Analysis Rubric",
                List.of(
                        new RubricCriterion(UUID.randomUUID(), "Correctness", "Meets the stated assignment requirements.", 50),
                        new RubricCriterion(UUID.randomUUID(), "Explanation", "Explains the design decisions clearly.", 50)
                )
        );
    }

    private Submission submission(User student, Assignment assignment, SubmissionType type, String content) {
        return new Submission(
                UUID.randomUUID(),
                assignment.getId(),
                student,
                type,
                content,
                Instant.now(),
                SubmissionStatus.ANALYZING
        );
    }

    private DemoReport reportSummary(Assignment assignment, AIAnalysisReport report) {
        Grade grade = report.getGradeSuggestion().orElseThrow();
        return new DemoReport(
                assignment.getTitle(),
                report.getSummary(),
                report.getRubricFindings().size(),
                report.getTestResults().size(),
                report.getSuggestedFeedback(),
                grade.points() + "/" + grade.maxPoints()
        );
    }

    public record Phase4DemoResponse(
            String student,
            DemoReport textReport,
            DemoReport codeReport,
            boolean cachedCodeReportReused,
            List<PatternTraceEvent> patternTraceEvents
    ) {
    }

    public record DemoReport(
            String assignment,
            String summary,
            int rubricFindingCount,
            int testResultCount,
            String suggestedFeedback,
            String gradeSuggestion
    ) {
    }
}
