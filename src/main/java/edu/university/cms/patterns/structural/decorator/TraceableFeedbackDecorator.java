package edu.university.cms.patterns.structural.decorator;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.AIAnalysisReport;
import edu.university.cms.domain.OfficialPattern;

public class TraceableFeedbackDecorator extends FeedbackGeneratorDecorator {

    private final PatternTraceService traceService;

    public TraceableFeedbackDecorator(FeedbackGenerator delegate, PatternTraceService traceService) {
        super(delegate);
        this.traceService = traceService;
    }

    @Override
    public String generateFeedback(AIAnalysisReport report) {
        if (traceService != null) {
            traceService.recordPhase4(
                    OfficialPattern.DECORATOR,
                    getClass().getSimpleName(),
                    "Enrich AI feedback",
                    "Feedback decorators layered rubric mapping and tone adjustment around base feedback",
                    "Generate AIAnalysisReport feedback"
            );
        }
        return super.generateFeedback(report);
    }
}
