package edu.university.cms.patterns.behavioral.strategy;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.CriterionScore;
import edu.university.cms.domain.Grade;
import edu.university.cms.domain.TestResult;

import java.util.List;

public class RubricWeightedGradingStrategy extends AbstractGradingStrategy {

    public RubricWeightedGradingStrategy(PatternTraceService traceService) {
        super(traceService);
    }

    @Override
    public Grade calculateGrade(Assignment assignment, List<CriterionScore> rubricFindings, List<TestResult> testResults) {
        int points = rubricFindings.stream().mapToInt(CriterionScore::pointsEarned).sum();
        points = Math.min(points, assignment.getMaxPoints());
        trace("Calculated grade from summed rubric criterion scores");
        return new Grade(points, assignment.getMaxPoints(), "Rubric-weighted grade suggestion.");
    }
}
