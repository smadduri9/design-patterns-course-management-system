package edu.university.cms.patterns.behavioral.template;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.Submission;
import edu.university.cms.patterns.behavioral.strategy.GradingStrategy;
import edu.university.cms.patterns.structural.adapter.AIClient;

import java.util.List;

public class TextSubmissionAnalyzer extends AbstractSubmissionAnalyzer {

    private final AIClient aiClient;

    public TextSubmissionAnalyzer(PatternTraceService traceService, AIClient aiClient, GradingStrategy gradingStrategy) {
        super(traceService, gradingStrategy);
        this.aiClient = aiClient;
    }

    @Override
    protected String analyzerType() {
        return "TextSubmissionAnalyzer";
    }

    @Override
    protected String prepareInput(Submission submission) {
        return submission.getContent().strip();
    }

    @Override
    protected SpecializedAnalysis runSpecializedAnalysis(String preparedInput, Assignment assignment) {
        return new SpecializedAnalysis(aiClient.analyzeText(preparedInput, assignment.getRubric()), List.of());
    }
}
