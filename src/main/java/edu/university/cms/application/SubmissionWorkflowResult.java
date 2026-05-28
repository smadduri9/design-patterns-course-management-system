package edu.university.cms.application;

import edu.university.cms.domain.AIAnalysisReport;
import edu.university.cms.domain.Submission;

public record SubmissionWorkflowResult(Submission submission, AIAnalysisReport analysisReport) {
}
