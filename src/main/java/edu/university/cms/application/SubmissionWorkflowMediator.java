package edu.university.cms.application;

import edu.university.cms.domain.AIAnalysisReport;
import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.OfficialPattern;
import edu.university.cms.domain.Submission;
import edu.university.cms.domain.SubmissionStatus;
import edu.university.cms.domain.User;
import edu.university.cms.domain.UserRole;
import edu.university.cms.patterns.behavioral.chain.AssignmentOpenValidationHandler;
import edu.university.cms.patterns.behavioral.chain.FileTypeValidationHandler;
import edu.university.cms.patterns.behavioral.chain.SizeValidationHandler;
import edu.university.cms.patterns.behavioral.chain.SubmissionTypeValidationHandler;
import edu.university.cms.patterns.behavioral.chain.SubmissionValidationContext;
import edu.university.cms.patterns.behavioral.chain.SubmissionValidationHandler;
import edu.university.cms.patterns.behavioral.strategy.GradingStrategySelector;
import edu.university.cms.patterns.structural.adapter.MockAIService;
import edu.university.cms.patterns.structural.adapter.MockAIServiceAdapter;
import edu.university.cms.patterns.structural.adapter.MockCodeSandbox;
import edu.university.cms.patterns.structural.adapter.MockCodeSandboxAdapter;
import edu.university.cms.patterns.structural.proxy.CachedAnalysisServiceProxy;
import edu.university.cms.patterns.structural.proxy.DefaultAnalysisService;
import edu.university.cms.repository.AssignmentRepository;
import edu.university.cms.repository.SubmissionRepository;
import edu.university.cms.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class SubmissionWorkflowMediator {

    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final PatternTraceService traceService;

    public SubmissionWorkflowMediator(
            UserRepository userRepository,
            AssignmentRepository assignmentRepository,
            SubmissionRepository submissionRepository,
            PatternTraceService traceService
    ) {
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.traceService = traceService;
    }

    public SubmissionWorkflowResult submit(SubmissionRequest request) {
        trace("Mediator started the submission workflow");
        User student = userRepository.findById(request.studentId())
                .orElseThrow(() -> new IllegalArgumentException("student was not found"));
        if (student.getRole() != UserRole.STUDENT) {
            throw new IllegalArgumentException("only a student can submit assignments");
        }

        Assignment assignment = assignmentRepository.findById(request.assignmentId())
                .orElseThrow(() -> new IllegalArgumentException("assignment was not found"));

        validate(student, assignment, request);

        Submission submission = new Submission(
                UUID.randomUUID(),
                assignment.getId(),
                student,
                request.submissionType(),
                request.content(),
                Instant.now(),
                SubmissionStatus.DRAFT
        );

        submission.submit(traceService);
        submissionRepository.save(submission);
        submission.startAnalysis(traceService);

        AIAnalysisReport analysisReport = createAnalysisService().analyze(submission, assignment);
        submission.setReport(analysisReport);

        submission.markAwaitingReview(traceService);
        submissionRepository.save(submission);
        trace("Mediator completed validation, persistence, state transitions, and analyzer selection");
        return new SubmissionWorkflowResult(submission, analysisReport);
    }

    private void validate(User student, Assignment assignment, SubmissionRequest request) {
        SubmissionValidationContext context = new SubmissionValidationContext(
                student,
                assignment,
                request.submissionType(),
                request.content()
        );
        SubmissionValidationHandler first = new SizeValidationHandler();
        SubmissionValidationHandler second = first.setNext(new AssignmentOpenValidationHandler());
        SubmissionValidationHandler third = second.setNext(new SubmissionTypeValidationHandler());
        third.setNext(new FileTypeValidationHandler());
        first.validate(context, traceService);
    }

    private CachedAnalysisServiceProxy createAnalysisService() {
        return new CachedAnalysisServiceProxy(
                new DefaultAnalysisService(
                        traceService,
                        new MockAIServiceAdapter(new MockAIService(), traceService),
                        new MockCodeSandboxAdapter(new MockCodeSandbox(), traceService),
                        new GradingStrategySelector()
                ),
                traceService
        );
    }

    private void trace(String description) {
        traceService.recordPhase3(
                OfficialPattern.MEDIATOR,
                getClass().getSimpleName(),
                "Coordinate submission workflow",
                description,
                "Student submits assignment"
        );
    }
}
