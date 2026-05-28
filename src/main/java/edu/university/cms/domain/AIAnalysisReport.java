package edu.university.cms.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class AIAnalysisReport {

    private final UUID id;
    private final String summary;
    private final List<CriterionScore> rubricFindings;
    private final List<TestResult> testResults;
    private final String suggestedFeedback;
    private final Grade gradeSuggestion;

    public AIAnalysisReport(
            UUID id,
            String summary,
            List<CriterionScore> rubricFindings,
            List<TestResult> testResults,
            String suggestedFeedback
    ) {
        this(id, summary, rubricFindings, testResults, suggestedFeedback, null);
    }

    public AIAnalysisReport(
            UUID id,
            String summary,
            List<CriterionScore> rubricFindings,
            List<TestResult> testResults,
            String suggestedFeedback,
            Grade gradeSuggestion
    ) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.summary = requireText(summary, "summary is required");
        this.rubricFindings = new ArrayList<>(Objects.requireNonNull(rubricFindings, "rubricFindings are required"));
        this.testResults = new ArrayList<>(Objects.requireNonNull(testResults, "testResults are required"));
        this.suggestedFeedback = requireText(suggestedFeedback, "suggestedFeedback is required");
        this.gradeSuggestion = gradeSuggestion;
    }

    public UUID getId() {
        return id;
    }

    public String getSummary() {
        return summary;
    }

    public List<CriterionScore> getRubricFindings() {
        return Collections.unmodifiableList(rubricFindings);
    }

    public List<TestResult> getTestResults() {
        return Collections.unmodifiableList(testResults);
    }

    public String getSuggestedFeedback() {
        return suggestedFeedback;
    }

    public Optional<Grade> getGradeSuggestion() {
        return Optional.ofNullable(gradeSuggestion);
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
