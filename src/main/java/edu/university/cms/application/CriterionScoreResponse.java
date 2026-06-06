package edu.university.cms.application;

import edu.university.cms.domain.CriterionScore;

import java.util.UUID;

public record CriterionScoreResponse(
        UUID criterionId,
        int pointsEarned,
        String feedback
) {

    public static CriterionScoreResponse from(CriterionScore score) {
        return new CriterionScoreResponse(score.criterionId(), score.pointsEarned(), score.feedback());
    }
}
