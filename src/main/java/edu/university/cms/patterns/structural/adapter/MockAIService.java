package edu.university.cms.patterns.structural.adapter;

import java.util.List;

public class MockAIService {

    public MockAIResult inspectDocument(MockDocumentPayload payload) {
        return new MockAIResult(
                "Mock summary: " + firstWords(payload.documentText(), 12),
                "Mock rubric mapping for: " + String.join(", ", payload.criteriaNames()),
                "Review the submission against the rubric and provide concise improvement notes."
        );
    }

    public MockAIResult explainTestRun(MockTestRunPayload payload) {
        long passed = payload.testLines().stream().filter(line -> line.contains("PASS")).count();
        long failed = payload.testLines().size() - passed;
        return new MockAIResult(
                "Mock code summary: " + passed + " tests passed and " + failed + " tests need attention.",
                "Mock code rubric mapping for: " + String.join(", ", payload.criteriaNames()),
                "Explain the test outcomes and ask the student to address failing cases."
        );
    }

    private String firstWords(String text, int limit) {
        String[] words = text.strip().split("\\s+");
        int count = Math.min(words.length, limit);
        return String.join(" ", List.of(words).subList(0, count));
    }

    public record MockDocumentPayload(String documentText, List<String> criteriaNames) {
    }

    public record MockTestRunPayload(List<String> testLines, List<String> criteriaNames) {
    }

    public record MockAIResult(String shortSummary, String criteriaMapping, String feedbackHint) {
    }
}
