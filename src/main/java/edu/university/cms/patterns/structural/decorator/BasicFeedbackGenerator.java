package edu.university.cms.patterns.structural.decorator;

import edu.university.cms.domain.AIAnalysisReport;

public class BasicFeedbackGenerator implements FeedbackGenerator {

    @Override
    public String generateFeedback(AIAnalysisReport report) {
        return report.getSuggestedFeedback();
    }
}
