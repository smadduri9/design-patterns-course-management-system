package edu.university.cms.patterns.structural.proxy;

import edu.university.cms.domain.AIAnalysisReport;
import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.Submission;

public interface AnalysisService {

    AIAnalysisReport analyze(Submission submission, Assignment assignment);
}
