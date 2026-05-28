package edu.university.cms.application;

import java.time.Instant;

public record TraceEventResponse(
        Instant timestamp,
        String userAction,
        String pattern,
        String patternDisplayName,
        String category,
        String className,
        String description,
        String workflowStep
) {

    public static TraceEventResponse from(PatternTraceEvent event) {
        return new TraceEventResponse(
                event.timestamp(),
                event.userAction(),
                event.pattern().name(),
                event.pattern().getDisplayName(),
                event.category().name(),
                event.className(),
                event.description(),
                event.workflowStep()
        );
    }
}
