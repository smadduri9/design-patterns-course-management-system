package edu.university.cms.patterns.structural.decorator;

import edu.university.cms.domain.AIAnalysisReport;

public class ToneAdjustedFeedbackDecorator extends FeedbackGeneratorDecorator {

    public ToneAdjustedFeedbackDecorator(FeedbackGenerator delegate) {
        super(delegate);
    }

    @Override
    public String generateFeedback(AIAnalysisReport report) {
        return super.generateFeedback(report) + " Tone: supportive and actionable.";
    }
}
