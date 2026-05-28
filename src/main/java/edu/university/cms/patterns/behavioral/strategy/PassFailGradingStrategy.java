package edu.university.cms.patterns.behavioral.strategy;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.CriterionScore;
import edu.university.cms.domain.Grade;
import edu.university.cms.domain.TestResult;

import java.util.List;

public class PassFailGradingStrategy extends AbstractGradingStrategy {

    public PassFailGradingStrategy(PatternTraceService traceService) {
        super(traceService);
    }

    @Override
    public Grade calculateGrade(Assignment assignment, List<CriterionScore> rubricFindings, List<TestResult> testResults) {
        int earned = rubricFindings.stream().mapToInt(CriterionScore::pointsEarned).sum();
        int possible = assignment.getRubric().getCriteria().stream().mapToInt(criterion -> criterion.getMaxPoints()).sum();
        boolean passed = possible == 0 || earned >= Math.ceil(possible * 0.6);
        trace("Calculated pass/fail grade from rubric threshold");
        return new Grade(passed ? assignment.getMaxPoints() : 0, assignment.getMaxPoints(), passed ? "Pass." : "Fail.");
    }
}
