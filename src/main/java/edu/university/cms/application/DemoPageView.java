package edu.university.cms.application;

import java.util.List;
import java.util.Map;

public record DemoPageView(
        String projectTitle,
        List<String> teamMembers,
        String overview,
        List<DemoWorkflowStepView> workflowSteps,
        List<ScenarioResultView> scenarioResults,
        ScenarioResultView codeSubmissionResult,
        ScenarioResultView textReportResult,
        ScenarioResultView codeReportResult,
        ScenarioResultView instructorReviewResult,
        ScenarioResultView studentFeedbackResult,
        List<DemoTraceView> traceEvents,
        Map<String, List<String>> patternsByCategory
) {
}
