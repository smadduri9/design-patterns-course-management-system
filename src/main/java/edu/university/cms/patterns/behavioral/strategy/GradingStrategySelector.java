package edu.university.cms.patterns.behavioral.strategy;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.GradingStrategyType;

public class GradingStrategySelector {

    public GradingStrategy select(GradingStrategyType type, PatternTraceService traceService) {
        return switch (type) {
            case RUBRIC_WEIGHTED -> new RubricWeightedGradingStrategy(traceService);
            case PASS_FAIL -> new PassFailGradingStrategy(traceService);
            case CODE_TEST -> new CodeTestGradingStrategy(traceService);
        };
    }
}
