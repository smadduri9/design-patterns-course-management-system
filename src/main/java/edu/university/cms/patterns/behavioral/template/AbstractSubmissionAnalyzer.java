package edu.university.cms.patterns.behavioral.template;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.AIAnalysisReport;
import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.CriterionScore;
import edu.university.cms.domain.Grade;
import edu.university.cms.domain.OfficialPattern;
import edu.university.cms.domain.Submission;
import edu.university.cms.domain.TestResult;
import edu.university.cms.patterns.behavioral.strategy.GradingStrategy;
import edu.university.cms.patterns.structural.adapter.AIResponse;
import edu.university.cms.patterns.structural.decorator.BasicFeedbackGenerator;
import edu.university.cms.patterns.structural.decorator.FeedbackGenerator;
import edu.university.cms.patterns.structural.decorator.RubricMappedFeedbackDecorator;
import edu.university.cms.patterns.structural.decorator.ToneAdjustedFeedbackDecorator;
import edu.university.cms.patterns.structural.decorator.TraceableFeedbackDecorator;

import java.util.List;
import java.util.UUID;

public abstract class AbstractSubmissionAnalyzer implements SubmissionAnalyzer {

    private final PatternTraceService traceService;
    private final GradingStrategy gradingStrategy;

    protected AbstractSubmissionAnalyzer(PatternTraceService traceService, GradingStrategy gradingStrategy) {
        this.traceService = traceService;
        this.gradingStrategy = gradingStrategy;
    }

    @Override
    public final AIAnalysisReport analyze(Submission submission, Assignment assignment) {
        trace("Template Method skeleton started for " + analyzerType());
        String preparedInput = prepareInput(submission);
        SpecializedAnalysis specializedAnalysis = runSpecializedAnalysis(preparedInput, assignment);
        List<CriterionScore> rubricFindings = mapToRubric(specializedAnalysis, assignment);
        AIAnalysisReport report = generateReport(specializedAnalysis, assignment, rubricFindings);
        trace("Template Method skeleton completed for " + analyzerType());
        return report;
    }

    protected abstract String analyzerType();

    protected abstract String prepareInput(Submission submission);

    protected abstract SpecializedAnalysis runSpecializedAnalysis(String preparedInput, Assignment assignment);

    protected List<CriterionScore> mapToRubric(SpecializedAnalysis analysis, Assignment assignment) {
        return assignment.getRubric().getCriteria().stream()
                .map(criterion -> new CriterionScore(
                        criterion.getId(),
                        (int) Math.round(criterion.getMaxPoints() * 0.8),
                        analysis.aiResponse().rubricMapping()
                ))
                .toList();
    }

    protected AIAnalysisReport generateReport(
            SpecializedAnalysis analysis,
            Assignment assignment,
            List<CriterionScore> rubricFindings
    ) {
        Grade grade = gradingStrategy.calculateGrade(assignment, rubricFindings, analysis.testResults());
        AIAnalysisReport baseReport = new AIAnalysisReport(
                UUID.randomUUID(),
                analysis.aiResponse().summary(),
                rubricFindings,
                analysis.testResults(),
                analysis.aiResponse().suggestedFeedback(),
                grade
        );
        FeedbackGenerator feedbackGenerator = new TraceableFeedbackDecorator(
                new ToneAdjustedFeedbackDecorator(
                        new RubricMappedFeedbackDecorator(new BasicFeedbackGenerator())
                ),
                traceService
        );
        String decoratedFeedback = feedbackGenerator.generateFeedback(baseReport);
        return new AIAnalysisReport(
                baseReport.getId(),
                baseReport.getSummary(),
                baseReport.getRubricFindings(),
                baseReport.getTestResults(),
                decoratedFeedback,
                grade
        );
    }

    private void trace(String description) {
        if (traceService != null) {
            traceService.recordPhase4(
                    OfficialPattern.TEMPLATE_METHOD,
                    getClass().getSimpleName(),
                    "Run mock submission analysis",
                    description,
                    "AIAnalysisReport generation"
            );
        }
    }

    protected record SpecializedAnalysis(AIResponse aiResponse, List<TestResult> testResults) {
    }
}
