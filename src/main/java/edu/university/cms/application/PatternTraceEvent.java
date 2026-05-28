package edu.university.cms.application;

import edu.university.cms.domain.OfficialPattern;
import edu.university.cms.domain.PatternCategory;

import java.time.Instant;

public record PatternTraceEvent(
        Instant timestamp,
        String userAction,
        OfficialPattern pattern,
        PatternCategory category,
        String className,
        String description,
        String workflowStep
) {
}
