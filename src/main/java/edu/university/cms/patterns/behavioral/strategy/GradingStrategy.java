package edu.university.cms.patterns.behavioral.strategy;

import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.CriterionScore;
import edu.university.cms.domain.Grade;
import edu.university.cms.domain.TestResult;

import java.util.List;

public interface GradingStrategy {

    Grade calculateGrade(Assignment assignment, List<CriterionScore> rubricFindings, List<TestResult> testResults);
}
