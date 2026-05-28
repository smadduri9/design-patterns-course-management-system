package edu.university.cms.patterns.structural.decorator;

import edu.university.cms.domain.AIAnalysisReport;

public class RubricMappedFeedbackDecorator extends FeedbackGeneratorDecorator {

    public RubricMappedFeedbackDecorator(FeedbackGenerator delegate) {
        super(delegate);
    }

    @Override
    public String generateFeedback(AIAnalysisReport report) {
        return super.generateFeedback(report)
                + " Rubric mapping included for "
                + report.getRubricFindings().size()
                + " criteria.";
    }
}
