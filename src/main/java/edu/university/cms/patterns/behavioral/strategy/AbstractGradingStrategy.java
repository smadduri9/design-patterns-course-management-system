package edu.university.cms.patterns.behavioral.strategy;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.OfficialPattern;

abstract class AbstractGradingStrategy implements GradingStrategy {

    private final PatternTraceService traceService;

    protected AbstractGradingStrategy(PatternTraceService traceService) {
        this.traceService = traceService;
    }

    protected void trace(String description) {
        if (traceService != null) {
            traceService.recordPhase4(
                    OfficialPattern.STRATEGY,
                    getClass().getSimpleName(),
                    "Calculate grade suggestion",
                    description,
                    "AI analysis grading"
            );
        }
    }
}
