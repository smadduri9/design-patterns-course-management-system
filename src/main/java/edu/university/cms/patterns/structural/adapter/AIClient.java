package edu.university.cms.patterns.structural.adapter;

import edu.university.cms.domain.Rubric;
import edu.university.cms.domain.TestResult;

import java.util.List;

public interface AIClient {

    AIResponse analyzeText(String content, Rubric rubric);

    AIResponse explainCodeResults(List<TestResult> testResults, Rubric rubric);
}
