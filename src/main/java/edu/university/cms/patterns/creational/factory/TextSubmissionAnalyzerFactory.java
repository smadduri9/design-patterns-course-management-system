package edu.university.cms.patterns.creational.factory;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.OfficialPattern;
import edu.university.cms.patterns.behavioral.strategy.GradingStrategy;
import edu.university.cms.patterns.behavioral.template.SubmissionAnalyzer;
import edu.university.cms.patterns.behavioral.template.TextSubmissionAnalyzer;
import edu.university.cms.patterns.structural.adapter.AIClient;
import edu.university.cms.patterns.structural.adapter.SandboxRunner;

public class TextSubmissionAnalyzerFactory implements SubmissionAnalyzerFactory {

    @Override
    public SubmissionAnalyzer createAnalyzer(
            PatternTraceService traceService,
            AIClient aiClient,
            SandboxRunner sandboxRunner,
            GradingStrategy gradingStrategy
    ) {
        if (traceService != null) {
            traceService.recordPhase4(
                    OfficialPattern.FACTORY_METHOD,
                    getClass().getSimpleName(),
                    "Select submission analyzer",
                    "Created text submission analyzer for PDF/text content",
                    "Submission type dispatch"
            );
        }
        return new TextSubmissionAnalyzer(traceService, aiClient, gradingStrategy);
    }
}
