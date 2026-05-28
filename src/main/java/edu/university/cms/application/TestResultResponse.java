package edu.university.cms.application;

import edu.university.cms.domain.TestResult;

public record TestResultResponse(
        String testName,
        boolean passed,
        String output
) {

    public static TestResultResponse from(TestResult result) {
        return new TestResultResponse(result.testName(), result.passed(), result.output());
    }
}
