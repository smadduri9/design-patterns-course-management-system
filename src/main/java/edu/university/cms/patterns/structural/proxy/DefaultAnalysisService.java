package edu.university.cms.patterns.structural.proxy;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.AIAnalysisReport;
import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.Submission;
import edu.university.cms.domain.SubmissionType;
import edu.university.cms.patterns.behavioral.strategy.GradingStrategy;
import edu.university.cms.patterns.behavioral.strategy.GradingStrategySelector;
import edu.university.cms.patterns.behavioral.template.SubmissionAnalyzer;
import edu.university.cms.patterns.creational.factory.CodeSubmissionAnalyzerFactory;
import edu.university.cms.patterns.creational.factory.SubmissionAnalyzerFactory;
import edu.university.cms.patterns.creational.factory.TextSubmissionAnalyzerFactory;
import edu.university.cms.patterns.structural.adapter.AIClient;
import edu.university.cms.patterns.structural.adapter.SandboxRunner;

public class DefaultAnalysisService implements AnalysisService {

    private final PatternTraceService traceService;
    private final AIClient aiClient;
    private final SandboxRunner sandboxRunner;
    private final GradingStrategySelector gradingStrategySelector;

    public DefaultAnalysisService(
            PatternTraceService traceService,
            AIClient aiClient,
            SandboxRunner sandboxRunner,
            GradingStrategySelector gradingStrategySelector
    ) {
        this.traceService = traceService;
        this.aiClient = aiClient;
        this.sandboxRunner = sandboxRunner;
        this.gradingStrategySelector = gradingStrategySelector;
    }

    @Override
    public AIAnalysisReport analyze(Submission submission, Assignment assignment) {
        GradingStrategy gradingStrategy = gradingStrategySelector.select(assignment.getGradingStrategyType(), traceService);
        SubmissionAnalyzerFactory factory = selectFactory(submission.getType());
        SubmissionAnalyzer analyzer = factory.createAnalyzer(traceService, aiClient, sandboxRunner, gradingStrategy);
        return analyzer.analyze(submission, assignment);
    }

    private SubmissionAnalyzerFactory selectFactory(SubmissionType type) {
        return switch (type) {
            case PDF_TEXT -> new TextSubmissionAnalyzerFactory();
            case JAVA_CODE -> new CodeSubmissionAnalyzerFactory();
        };
    }
}
