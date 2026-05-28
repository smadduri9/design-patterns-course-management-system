package edu.university.cms.patterns.structural.decorator;

import edu.university.cms.domain.AIAnalysisReport;

public interface FeedbackGenerator {

    String generateFeedback(AIAnalysisReport report);
}
