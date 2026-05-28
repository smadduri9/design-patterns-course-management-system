package edu.university.cms;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.AIAnalysisReport;
import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.CriterionScore;
import edu.university.cms.domain.Grade;
import edu.university.cms.domain.GradingStrategyType;
import edu.university.cms.domain.OfficialPattern;
import edu.university.cms.domain.Rubric;
import edu.university.cms.domain.RubricCriterion;
import edu.university.cms.domain.Submission;
import edu.university.cms.domain.SubmissionStatus;
import edu.university.cms.domain.SubmissionType;
import edu.university.cms.domain.TestResult;
import edu.university.cms.domain.User;
import edu.university.cms.domain.UserRole;
import edu.university.cms.patterns.behavioral.strategy.CodeTestGradingStrategy;
import edu.university.cms.patterns.behavioral.strategy.PassFailGradingStrategy;
import edu.university.cms.patterns.behavioral.strategy.RubricWeightedGradingStrategy;
import edu.university.cms.patterns.behavioral.template.CodeSubmissionAnalyzer;
import edu.university.cms.patterns.behavioral.template.TextSubmissionAnalyzer;
import edu.university.cms.patterns.creational.builder.AssignmentBuilder;
import edu.university.cms.patterns.structural.adapter.AIResponse;
import edu.university.cms.patterns.structural.adapter.MockAIService;
import edu.university.cms.patterns.structural.adapter.MockAIServiceAdapter;
import edu.university.cms.patterns.structural.adapter.MockCodeSandbox;
import edu.university.cms.patterns.structural.adapter.MockCodeSandboxAdapter;
import edu.university.cms.patterns.structural.decorator.BasicFeedbackGenerator;
import edu.university.cms.patterns.structural.decorator.FeedbackGenerator;
import edu.university.cms.patterns.structural.decorator.RubricMappedFeedbackDecorator;
import edu.university.cms.patterns.structural.decorator.ToneAdjustedFeedbackDecorator;
import edu.university.cms.patterns.structural.proxy.AnalysisService;
import edu.university.cms.patterns.structural.proxy.CachedAnalysisServiceProxy;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Phase4AnalysisTest {

    @Test
    void mockAIServiceAdapterReturnsAIClientCompatibleResults() {
        PatternTraceService traceService = new PatternTraceService();
        MockAIServiceAdapter adapter = new MockAIServiceAdapter(new MockAIService(), traceService);

        AIResponse response = adapter.analyzeText("Adapter pattern keeps APIs separate.", rubric());

        assertThat(response.summary()).contains("Mock summary");
        assertThat(response.rubricMapping()).contains("Correctness");
        assertThat(traceService.findAll()).extracting(event -> event.pattern()).containsExactly(OfficialPattern.ADAPTER);
    }

    @Test
    void mockCodeSandboxAdapterReturnsSandboxRunnerCompatibleResults() {
        PatternTraceService traceService = new PatternTraceService();
        MockCodeSandboxAdapter adapter = new MockCodeSandboxAdapter(new MockCodeSandbox(), traceService);

        List<TestResult> results = adapter.runTests("public class Demo { String value() { return \"ok\"; } }");

        assertThat(results).hasSize(2);
        assertThat(results).allMatch(TestResult::passed);
        assertThat(traceService.findAll()).extracting(event -> event.pattern()).containsExactly(OfficialPattern.ADAPTER);
    }

    @Test
    void cachedAnalysisServiceProxyAvoidsRepeatedAnalysisForSameSubmission() {
        PatternTraceService traceService = new PatternTraceService();
        AtomicInteger calls = new AtomicInteger();
        AIAnalysisReport report = report(new Grade(80, 100, "cached"));
        AnalysisService delegate = (submission, assignment) -> {
            calls.incrementAndGet();
            return report;
        };
        CachedAnalysisServiceProxy proxy = new CachedAnalysisServiceProxy(delegate, traceService);
        Submission submission = submission(SubmissionType.PDF_TEXT, "Text content");
        Assignment assignment = assignment(SubmissionType.PDF_TEXT, GradingStrategyType.RUBRIC_WEIGHTED);

        AIAnalysisReport first = proxy.analyze(submission, assignment);
        AIAnalysisReport second = proxy.analyze(submission, assignment);

        assertThat(first).isSameAs(second);
        assertThat(calls).hasValue(1);
        assertThat(traceService.findAll()).extracting(event -> event.pattern()).containsOnly(OfficialPattern.PROXY);
    }

    @Test
    void feedbackDecoratorsEnrichOutputInExpectedOrder() {
        FeedbackGenerator generator = new ToneAdjustedFeedbackDecorator(
                new RubricMappedFeedbackDecorator(new BasicFeedbackGenerator())
        );

        String feedback = generator.generateFeedback(report(new Grade(90, 100, "grade")));

        assertThat(feedback).containsSubsequence(
                "Base feedback.",
                "Rubric mapping included",
                "Tone: supportive and actionable."
        );
    }

    @Test
    void rubricWeightedGradingStrategyCalculatesExpectedGrade() {
        Grade grade = new RubricWeightedGradingStrategy(new PatternTraceService()).calculateGrade(
                assignment(SubmissionType.PDF_TEXT, GradingStrategyType.RUBRIC_WEIGHTED),
                List.of(new CriterionScore(UUID.randomUUID(), 35, "ok"), new CriterionScore(UUID.randomUUID(), 40, "ok")),
                List.of()
        );

        assertThat(grade.points()).isEqualTo(75);
    }

    @Test
    void passFailGradingStrategyDiffersFromRubricWeighted() {
        Assignment assignment = assignment(SubmissionType.PDF_TEXT, GradingStrategyType.PASS_FAIL);
        List<CriterionScore> scores = List.of(new CriterionScore(UUID.randomUUID(), 35, "partial"));

        Grade passFail = new PassFailGradingStrategy(new PatternTraceService()).calculateGrade(assignment, scores, List.of());
        Grade weighted = new RubricWeightedGradingStrategy(new PatternTraceService()).calculateGrade(assignment, scores, List.of());

        assertThat(passFail.points()).isEqualTo(0);
        assertThat(weighted.points()).isEqualTo(35);
    }

    @Test
    void codeTestGradingStrategyUsesMockTestResults() {
        Grade grade = new CodeTestGradingStrategy(new PatternTraceService()).calculateGrade(
                assignment(SubmissionType.JAVA_CODE, GradingStrategyType.CODE_TEST),
                List.of(),
                List.of(new TestResult("one", true, "ok"), new TestResult("two", false, "missing"))
        );

        assertThat(grade.points()).isEqualTo(50);
    }

    @Test
    void textSubmissionAnalyzerReturnsRealAIAnalysisReport() {
        PatternTraceService traceService = new PatternTraceService();
        Assignment assignment = assignment(SubmissionType.PDF_TEXT, GradingStrategyType.RUBRIC_WEIGHTED);

        AIAnalysisReport report = new TextSubmissionAnalyzer(
                traceService,
                new MockAIServiceAdapter(new MockAIService(), traceService),
                new RubricWeightedGradingStrategy(traceService)
        ).analyze(submission(SubmissionType.PDF_TEXT, "This text explains the Adapter pattern."), assignment);

        assertThat(report.getSummary()).contains("Mock summary");
        assertThat(report.getRubricFindings()).isNotEmpty();
        assertThat(report.getTestResults()).isEmpty();
        assertThat(report.getGradeSuggestion()).isPresent();
    }

    @Test
    void codeSubmissionAnalyzerReturnsReportWithTestResults() {
        PatternTraceService traceService = new PatternTraceService();
        Assignment assignment = assignment(SubmissionType.JAVA_CODE, GradingStrategyType.CODE_TEST);

        AIAnalysisReport report = new CodeSubmissionAnalyzer(
                traceService,
                new MockAIServiceAdapter(new MockAIService(), traceService),
                new MockCodeSandboxAdapter(new MockCodeSandbox(), traceService),
                new CodeTestGradingStrategy(traceService)
        ).analyze(submission(SubmissionType.JAVA_CODE, "public class Demo { String value() { return \"ok\"; } }"), assignment);

        assertThat(report.getSummary()).contains("Mock code summary");
        assertThat(report.getTestResults()).hasSize(2);
        assertThat(report.getGradeSuggestion()).isPresent();
    }

    @Test
    void patternTraceServiceRecordsOnlyOfficialPhase4Patterns() {
        PatternTraceService traceService = new PatternTraceService();

        traceService.recordPhase4(OfficialPattern.ADAPTER, "Adapter", "Action", "Description", "Workflow");

        assertThatThrownBy(() -> traceService.recordPhase4(
                OfficialPattern.STATE,
                "State",
                "Action",
                "Description",
                "Workflow"
        )).isInstanceOf(IllegalArgumentException.class);
        assertThat(traceService.findAll()).hasSize(1);
    }

    private static Assignment assignment(SubmissionType type, GradingStrategyType strategyType) {
        return new AssignmentBuilder()
                .title("Phase 4 Assignment")
                .description("Analyze this submission.")
                .dueDate(LocalDate.now().plusDays(7))
                .acceptedSubmissionType(type)
                .rubric(rubric())
                .maxPoints(100)
                .gradingStrategyType(strategyType)
                .build();
    }

    private static Rubric rubric() {
        return new Rubric(
                UUID.randomUUID(),
                "Phase 4 Rubric",
                List.of(
                        new RubricCriterion(UUID.randomUUID(), "Correctness", "Meets requirements.", 50),
                        new RubricCriterion(UUID.randomUUID(), "Clarity", "Explains choices.", 50)
                )
        );
    }

    private static Submission submission(SubmissionType type, String content) {
        return new Submission(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new User(UUID.randomUUID(), "Demo Student", UserRole.STUDENT),
                type,
                content,
                Instant.now(),
                SubmissionStatus.ANALYZING
        );
    }

    private static AIAnalysisReport report(Grade grade) {
        return new AIAnalysisReport(
                UUID.randomUUID(),
                "Summary",
                List.of(new CriterionScore(UUID.randomUUID(), 50, "ok")),
                List.of(),
                "Base feedback.",
                grade
        );
    }
}
