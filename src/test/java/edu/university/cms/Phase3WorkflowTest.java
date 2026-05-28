package edu.university.cms;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.application.SubmissionWorkflowFacade;
import edu.university.cms.application.SubmissionWorkflowMediator;
import edu.university.cms.application.SubmissionWorkflowResult;
import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.AIAnalysisReport;
import edu.university.cms.domain.OfficialPattern;
import edu.university.cms.domain.Rubric;
import edu.university.cms.domain.RubricCriterion;
import edu.university.cms.domain.Submission;
import edu.university.cms.domain.SubmissionStatus;
import edu.university.cms.domain.SubmissionType;
import edu.university.cms.domain.User;
import edu.university.cms.domain.UserRole;
import edu.university.cms.patterns.behavioral.chain.AssignmentOpenValidationHandler;
import edu.university.cms.patterns.behavioral.chain.FileTypeValidationHandler;
import edu.university.cms.patterns.behavioral.chain.SizeValidationHandler;
import edu.university.cms.patterns.behavioral.chain.SubmissionTypeValidationHandler;
import edu.university.cms.patterns.behavioral.chain.SubmissionValidationContext;
import edu.university.cms.patterns.behavioral.chain.SubmissionValidationHandler;
import edu.university.cms.patterns.behavioral.template.CodeSubmissionAnalyzer;
import edu.university.cms.patterns.behavioral.template.TextSubmissionAnalyzer;
import edu.university.cms.patterns.behavioral.strategy.RubricWeightedGradingStrategy;
import edu.university.cms.patterns.creational.builder.AssignmentBuilder;
import edu.university.cms.patterns.creational.factory.CodeSubmissionAnalyzerFactory;
import edu.university.cms.patterns.creational.factory.TextSubmissionAnalyzerFactory;
import edu.university.cms.patterns.structural.adapter.MockAIService;
import edu.university.cms.patterns.structural.adapter.MockAIServiceAdapter;
import edu.university.cms.patterns.structural.adapter.MockCodeSandbox;
import edu.university.cms.patterns.structural.adapter.MockCodeSandboxAdapter;
import edu.university.cms.repository.AssignmentRepository;
import edu.university.cms.repository.SubmissionRepository;
import edu.university.cms.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Phase3WorkflowTest {

    @Test
    void validTextSubmissionPassesValidation() {
        PatternTraceService traceService = new PatternTraceService();
        SubmissionValidationHandler chain = validationChain();

        chain.validate(
                new SubmissionValidationContext(
                        student(),
                        assignment("Text Assignment", Set.of(SubmissionType.PDF_TEXT)),
                        SubmissionType.PDF_TEXT,
                        "This is a valid text submission."
                ),
                traceService
        );

        assertThat(traceService.findAll())
                .extracting(event -> event.className())
                .containsExactly(
                        "SizeValidationHandler",
                        "AssignmentOpenValidationHandler",
                        "SubmissionTypeValidationHandler",
                        "FileTypeValidationHandler"
                );
    }

    @Test
    void invalidSubmissionTypeOrEmptyContentIsRejected() {
        Assignment textOnlyAssignment = assignment("Text Assignment", Set.of(SubmissionType.PDF_TEXT));

        assertThatThrownBy(() -> validationChain().validate(
                new SubmissionValidationContext(student(), textOnlyAssignment, SubmissionType.PDF_TEXT, " "),
                new PatternTraceService()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("content is required");

        assertThatThrownBy(() -> validationChain().validate(
                new SubmissionValidationContext(
                        student(),
                        textOnlyAssignment,
                        SubmissionType.JAVA_CODE,
                        "public class Demo {}"
                ),
                new PatternTraceService()
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not accept");
    }

    @Test
    void submissionStateTransitionsHappenInExpectedOrder() {
        PatternTraceService traceService = new PatternTraceService();
        Submission submission = submission(SubmissionStatus.DRAFT);

        submission.submit(traceService);
        assertThat(submission.getStatus()).isEqualTo(SubmissionStatus.SUBMITTED);

        submission.startAnalysis(traceService);
        assertThat(submission.getStatus()).isEqualTo(SubmissionStatus.ANALYZING);

        submission.markAwaitingReview(traceService);
        assertThat(submission.getStatus()).isEqualTo(SubmissionStatus.AWAITING_REVIEW);

        assertThat(traceService.findAll())
                .extracting(event -> event.description())
                .containsExactly(
                        "Submission moved from Draft to Submitted",
                        "Submission moved from Submitted to Analyzing",
                        "Submission moved from Analyzing to AwaitingReview"
                );
    }

    @Test
    void mediatorCoordinatesSubmissionWorkflow() {
        TestWorkflow workflow = workflow(Set.of(SubmissionType.PDF_TEXT));

        SubmissionWorkflowResult result = workflow.facade().submitAssignment(
                workflow.student().getId(),
                workflow.assignment().getId(),
                SubmissionType.PDF_TEXT,
                "A text submission about design patterns."
        );

        assertThat(result.submission().getStatus()).isEqualTo(SubmissionStatus.AWAITING_REVIEW);
        assertThat(result.submission().getReport()).isPresent();
        assertThat(workflow.submissionRepository().findById(result.submission().getId())).contains(result.submission());
        assertThat(workflow.traceService().findAll())
                .extracting(event -> event.pattern())
                .contains(
                        OfficialPattern.FACADE,
                        OfficialPattern.MEDIATOR,
                        OfficialPattern.CHAIN_OF_RESPONSIBILITY,
                        OfficialPattern.STATE,
                        OfficialPattern.FACTORY_METHOD,
                        OfficialPattern.TEMPLATE_METHOD
                );
    }

    @Test
    void facadeExposesSimpleSubmitMethod() {
        TestWorkflow workflow = workflow(Set.of(SubmissionType.JAVA_CODE));

        SubmissionWorkflowResult result = workflow.facade().submitAssignment(
                workflow.student().getId(),
                workflow.assignment().getId(),
                SubmissionType.JAVA_CODE,
                "public class DemoSubmission {}"
        );

        assertThat(result.analysisReport().getTestResults()).isNotEmpty();
        assertThat(result.submission().getStatus()).isEqualTo(SubmissionStatus.AWAITING_REVIEW);
    }

    @Test
    void factoryMethodSelectsTextAndCodeAnalyzers() {
        PatternTraceService traceService = new PatternTraceService();

        assertThat(new TextSubmissionAnalyzerFactory().createAnalyzer(
                traceService,
                new MockAIServiceAdapter(new MockAIService(), traceService),
                new MockCodeSandboxAdapter(new MockCodeSandbox(), traceService),
                new RubricWeightedGradingStrategy(traceService)
        ))
                .isInstanceOf(TextSubmissionAnalyzer.class);
        assertThat(new CodeSubmissionAnalyzerFactory().createAnalyzer(
                traceService,
                new MockAIServiceAdapter(new MockAIService(), traceService),
                new MockCodeSandboxAdapter(new MockCodeSandbox(), traceService),
                new RubricWeightedGradingStrategy(traceService)
        ))
                .isInstanceOf(CodeSubmissionAnalyzer.class);

        assertThat(traceService.findAll())
                .extracting(event -> event.pattern())
                .contains(OfficialPattern.FACTORY_METHOD, OfficialPattern.FACTORY_METHOD);
    }

    @Test
    void templateMethodSkeletonIsUsedByBothAnalyzerTypes() {
        PatternTraceService traceService = new PatternTraceService();
        Submission textSubmission = submission(SubmissionStatus.ANALYZING, SubmissionType.PDF_TEXT, "Text content");
        Submission codeSubmission = submission(SubmissionStatus.ANALYZING, SubmissionType.JAVA_CODE, "public class Demo {}");

        Assignment textAssignment = assignment("Text", Set.of(SubmissionType.PDF_TEXT));
        Assignment codeAssignment = assignment("Code", Set.of(SubmissionType.JAVA_CODE));
        AIAnalysisReport textAnalysis = new TextSubmissionAnalyzer(
                traceService,
                new MockAIServiceAdapter(new MockAIService(), traceService),
                new RubricWeightedGradingStrategy(traceService)
        ).analyze(textSubmission, textAssignment);
        AIAnalysisReport codeAnalysis = new CodeSubmissionAnalyzer(
                traceService,
                new MockAIServiceAdapter(new MockAIService(), traceService),
                new MockCodeSandboxAdapter(new MockCodeSandbox(), traceService),
                new RubricWeightedGradingStrategy(traceService)
        ).analyze(codeSubmission, codeAssignment);

        assertThat(textAnalysis.getSummary()).contains("Mock summary");
        assertThat(codeAnalysis.getTestResults()).isNotEmpty();
        assertThat(traceService.findAll())
                .extracting(event -> event.pattern())
                .contains(OfficialPattern.TEMPLATE_METHOD);
    }

    @Test
    void patternTraceServiceRecordsOnlyOfficialPhase3Patterns() {
        PatternTraceService traceService = new PatternTraceService();

        traceService.recordPhase3(
                OfficialPattern.STATE,
                "TestState",
                "Test action",
                "Test description",
                "Test workflow"
        );

        assertThatThrownBy(() -> traceService.recordPhase3(
                OfficialPattern.COMPOSITE,
                "TestComposite",
                "Test action",
                "Test description",
                "Test workflow"
        )).isInstanceOf(IllegalArgumentException.class);
        assertThat(traceService.findAll()).hasSize(1);
    }

    private static SubmissionValidationHandler validationChain() {
        SubmissionValidationHandler first = new SizeValidationHandler();
        SubmissionValidationHandler second = first.setNext(new AssignmentOpenValidationHandler());
        SubmissionValidationHandler third = second.setNext(new SubmissionTypeValidationHandler());
        third.setNext(new FileTypeValidationHandler());
        return first;
    }

    private static TestWorkflow workflow(Set<SubmissionType> acceptedTypes) {
        User student = student();
        UserRepository userRepository = new UserRepository();
        AssignmentRepository assignmentRepository = new AssignmentRepository();
        SubmissionRepository submissionRepository = new SubmissionRepository();
        PatternTraceService traceService = new PatternTraceService();
        Assignment assignment = assignment("Workflow Assignment", acceptedTypes);

        userRepository.save(student);
        assignmentRepository.save(assignment);

        SubmissionWorkflowMediator mediator = new SubmissionWorkflowMediator(
                userRepository,
                assignmentRepository,
                submissionRepository,
                traceService
        );
        SubmissionWorkflowFacade facade = new SubmissionWorkflowFacade(mediator, traceService);
        return new TestWorkflow(student, assignment, submissionRepository, traceService, facade);
    }

    private static User student() {
        return new User(UUID.randomUUID(), "Demo Student", UserRole.STUDENT);
    }

    private static Assignment assignment(String title, Set<SubmissionType> acceptedTypes) {
        AssignmentBuilder builder = new AssignmentBuilder()
                .title(title)
                .description("Phase 3 assignment.")
                .dueDate(LocalDate.now().plusDays(7))
                .rubric(rubric())
                .maxPoints(100);
        EnumSet.copyOf(acceptedTypes).forEach(builder::acceptedSubmissionType);
        return builder.build();
    }

    private static Rubric rubric() {
        return new Rubric(
                UUID.randomUUID(),
                "Workflow Rubric",
                List.of(new RubricCriterion(UUID.randomUUID(), "Completeness", "Complete submission.", 100))
        );
    }

    private static Submission submission(SubmissionStatus status) {
        return submission(status, SubmissionType.PDF_TEXT, "Submission content");
    }

    private static Submission submission(SubmissionStatus status, SubmissionType type, String content) {
        return new Submission(
                UUID.randomUUID(),
                UUID.randomUUID(),
                student(),
                type,
                content,
                Instant.now(),
                status
        );
    }

    private record TestWorkflow(
            User student,
            Assignment assignment,
            SubmissionRepository submissionRepository,
            PatternTraceService traceService,
            SubmissionWorkflowFacade facade
    ) {
    }
}
