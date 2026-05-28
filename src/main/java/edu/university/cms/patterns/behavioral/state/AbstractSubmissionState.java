package edu.university.cms.patterns.behavioral.state;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.OfficialPattern;

abstract class AbstractSubmissionState implements SubmissionState {

    protected void trace(
            PatternTraceService traceService,
            String className,
            String description,
            String workflowStep
    ) {
        if (traceService != null) {
            traceService.recordPhase3(
                    OfficialPattern.STATE,
                    className,
                    "Transition submission state",
                    description,
                    workflowStep
            );
        }
    }
}
