package edu.university.cms.domain;

public record TestResult(String testName, boolean passed, String output) {

    public TestResult {
        if (testName == null || testName.isBlank()) {
            throw new IllegalArgumentException("testName is required");
        }
        output = output == null ? "" : output;
    }
}
