package edu.university.cms.patterns.behavioral.strategy;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.CriterionScore;
import edu.university.cms.domain.Grade;
import edu.university.cms.domain.TestResult;

import java.util.List;

public class CodeTestGradingStrategy extends AbstractGradingStrategy {

    public CodeTestGradingStrategy(PatternTraceService traceService) {
        super(traceService);
    }

    @Override
    public Grade calculateGrade(Assignment assignment, List<CriterionScore> rubricFindings, List<TestResult> testResults) {
        long passed = testResults.stream().filter(TestResult::passed).count();
        int points = testResults.isEmpty()
                ? 0
                : (int) Math.round((passed * 1.0 / testResults.size()) * assignment.getMaxPoints());
        trace("Calculated code grade from mock test pass ratio");
        return new Grade(points, assignment.getMaxPoints(), passed + " of " + testResults.size() + " mock tests passed.");
    }
}
