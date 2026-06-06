package edu.university.cms.patterns.behavioral.template;

import edu.university.cms.domain.AIAnalysisReport;
import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.Submission;

public interface SubmissionAnalyzer {

    AIAnalysisReport analyze(Submission submission, Assignment assignment);
}
