package edu.university.cms.patterns.structural.decorator;

import edu.university.cms.domain.AIAnalysisReport;

import java.util.Objects;

abstract class FeedbackGeneratorDecorator implements FeedbackGenerator {

    private final FeedbackGenerator delegate;

    protected FeedbackGeneratorDecorator(FeedbackGenerator delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate is required");
    }

    @Override
    public String generateFeedback(AIAnalysisReport report) {
        return delegate.generateFeedback(report);
    }
}
