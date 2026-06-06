package edu.university.cms.application;

import edu.university.cms.domain.AIAnalysisReport;

import java.util.List;
import java.util.UUID;

public record AIAnalysisReportResponse(
        UUID id,
        String summary,
        List<CriterionScoreResponse> rubricFindings,
        List<TestResultResponse> testResults,
        String suggestedFeedback,
        GradeResponse gradeSuggestion
) {

    public static AIAnalysisReportResponse from(AIAnalysisReport report) {
        return new AIAnalysisReportResponse(
                report.getId(),
                report.getSummary(),
                report.getRubricFindings().stream()
                        .map(CriterionScoreResponse::from)
                        .toList(),
                report.getTestResults().stream()
                        .map(TestResultResponse::from)
                        .toList(),
                report.getSuggestedFeedback(),
                report.getGradeSuggestion()
                        .map(GradeResponse::from)
                        .orElse(null)
        );
    }
}
