package edu.university.cms.patterns.structural.adapter;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.OfficialPattern;
import edu.university.cms.domain.Rubric;
import edu.university.cms.domain.TestResult;

import java.util.List;

public class MockAIServiceAdapter implements AIClient {

    private final MockAIService mockAIService;
    private final PatternTraceService traceService;

    public MockAIServiceAdapter(MockAIService mockAIService, PatternTraceService traceService) {
        this.mockAIService = mockAIService;
        this.traceService = traceService;
    }

    @Override
    public AIResponse analyzeText(String content, Rubric rubric) {
        trace("Adapted internal text analysis request to MockAIService document payload");
        MockAIService.MockAIResult result = mockAIService.inspectDocument(
                new MockAIService.MockDocumentPayload(content, criteriaNames(rubric))
        );
        return new AIResponse(result.shortSummary(), result.criteriaMapping(), result.feedbackHint());
    }

    @Override
    public AIResponse explainCodeResults(List<TestResult> testResults, Rubric rubric) {
        trace("Adapted internal code test results to MockAIService test-run payload");
        MockAIService.MockAIResult result = mockAIService.explainTestRun(
                new MockAIService.MockTestRunPayload(testResultLines(testResults), criteriaNames(rubric))
        );
        return new AIResponse(result.shortSummary(), result.criteriaMapping(), result.feedbackHint());
    }

    private List<String> criteriaNames(Rubric rubric) {
        return rubric.getCriteria().stream().map(criterion -> criterion.getName()).toList();
    }

    private List<String> testResultLines(List<TestResult> testResults) {
        return testResults.stream()
                .map(result -> (result.passed() ? "PASS " : "FAIL ") + result.testName() + ": " + result.output())
                .toList();
    }

    private void trace(String description) {
        if (traceService != null) {
            traceService.recordPhase4(
                    OfficialPattern.ADAPTER,
                    getClass().getSimpleName(),
                    "Call mock AI service",
                    description,
                    "Mock AI analysis"
            );
        }
    }
}
