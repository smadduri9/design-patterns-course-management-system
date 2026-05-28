package edu.university.cms.patterns.structural.adapter;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.OfficialPattern;
import edu.university.cms.domain.TestResult;

import java.util.List;

public class MockCodeSandboxAdapter implements SandboxRunner {

    private final MockCodeSandbox mockCodeSandbox;
    private final PatternTraceService traceService;

    public MockCodeSandboxAdapter(MockCodeSandbox mockCodeSandbox, PatternTraceService traceService) {
        this.mockCodeSandbox = mockCodeSandbox;
        this.traceService = traceService;
    }

    @Override
    public List<TestResult> runTests(String javaSource) {
        trace("Adapted internal Java source request to MockCodeSandbox execution payload");
        MockCodeSandbox.MockSandboxResponse response = mockCodeSandbox.execute(
                new MockCodeSandbox.MockSandboxRequest(javaSource, "java")
        );
        return response.cases().stream()
                .map(testCase -> new TestResult(testCase.name(), testCase.ok(), testCase.details()))
                .toList();
    }

    private void trace(String description) {
        if (traceService != null) {
            traceService.recordPhase4(
                    OfficialPattern.ADAPTER,
                    getClass().getSimpleName(),
                    "Call mock sandbox",
                    description,
                    "Mock code test run"
            );
        }
    }
}
