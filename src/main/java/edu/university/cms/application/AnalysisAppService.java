package edu.university.cms.application;

import edu.university.cms.domain.AIAnalysisReport;
import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.Submission;
import edu.university.cms.domain.SubmissionStatus;
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
import edu.university.cms.repository.SubmissionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AnalysisAppService {

    private final SubmissionAppService submissionAppService;
    private final SubmissionRepository submissionRepository;
    private final PatternTraceService traceService;

    public AnalysisAppService(
            SubmissionAppService submissionAppService,
            SubmissionRepository submissionRepository,
            PatternTraceService traceService
    ) {
        this.submissionAppService = submissionAppService;
        this.submissionRepository = submissionRepository;
        this.traceService = traceService;
    }

    public AnalysisResponse analyzeSubmission(java.util.UUID submissionId) {
        Submission submission = submissionAppService.findSubmission(submissionId);
        if (submission.getReport().isPresent()) {
            return AnalysisResponse.from(submission);
        }

        if (submission.getStatus() != SubmissionStatus.SUBMITTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "submission is not ready for analysis");
        }

        Assignment assignment = submissionAppService.findAssignment(submission.getAssignmentId());
        validate(submission, assignment);
        submission.startAnalysis(traceService);
        AIAnalysisReport report = createAnalysisService().analyze(submission, assignment);
        submission.setReport(report);
        submission.markAwaitingReview(traceService);
        submissionRepository.save(submission);
        return AnalysisResponse.from(submission);
    }

    private void validate(Submission submission, Assignment assignment) {
        SubmissionValidationContext context = new SubmissionValidationContext(
                submission.getStudent(),
                assignment,
                submission.getType(),
                submission.getContent()
        );
        SubmissionValidationHandler first = new SizeValidationHandler();
        SubmissionValidationHandler second = first.setNext(new AssignmentOpenValidationHandler());
        SubmissionValidationHandler third = second.setNext(new SubmissionTypeValidationHandler());
        third.setNext(new FileTypeValidationHandler());
        try {
            first.validate(context, traceService);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
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
}
