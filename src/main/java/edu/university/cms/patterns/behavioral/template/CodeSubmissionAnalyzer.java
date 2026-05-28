package edu.university.cms.patterns.behavioral.template;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.CriterionScore;
import edu.university.cms.domain.Submission;
import edu.university.cms.domain.TestResult;
import edu.university.cms.patterns.behavioral.strategy.GradingStrategy;
import edu.university.cms.patterns.structural.adapter.AIClient;
import edu.university.cms.patterns.structural.adapter.SandboxRunner;

import java.util.List;

public class CodeSubmissionAnalyzer extends AbstractSubmissionAnalyzer {

    private final AIClient aiClient;
    private final SandboxRunner sandboxRunner;

    public CodeSubmissionAnalyzer(
            PatternTraceService traceService,
            AIClient aiClient,
            SandboxRunner sandboxRunner,
            GradingStrategy gradingStrategy
    ) {
        super(traceService, gradingStrategy);
        this.aiClient = aiClient;
        this.sandboxRunner = sandboxRunner;
    }

    @Override
    protected String analyzerType() {
        return "CodeSubmissionAnalyzer";
    }

    @Override
    protected String prepareInput(Submission submission) {
        return submission.getContent().strip();
    }

    @Override
    protected SpecializedAnalysis runSpecializedAnalysis(String preparedInput, Assignment assignment) {
        List<TestResult> testResults = sandboxRunner.runTests(preparedInput);
        return new SpecializedAnalysis(aiClient.explainCodeResults(testResults, assignment.getRubric()), testResults);
    }

    @Override
    protected List<CriterionScore> mapToRubric(SpecializedAnalysis analysis, Assignment assignment) {
        long passed = analysis.testResults().stream().filter(TestResult::passed).count();
        double ratio = analysis.testResults().isEmpty() ? 0.0 : passed * 1.0 / analysis.testResults().size();
        return assignment.getRubric().getCriteria().stream()
                .map(criterion -> new CriterionScore(
                        criterion.getId(),
                        (int) Math.round(criterion.getMaxPoints() * ratio),
                        analysis.aiResponse().rubricMapping()
                ))
                .toList();
    }
}
