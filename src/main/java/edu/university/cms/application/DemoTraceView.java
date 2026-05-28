package edu.university.cms.application;

public record DemoTraceView(
        String timestamp,
        String userAction,
        String pattern,
        String category,
        String className,
        String description,
        String workflowStep
) {
}
