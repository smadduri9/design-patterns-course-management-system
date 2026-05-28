package edu.university.cms.domain;

import java.util.UUID;

public record CriterionScore(UUID criterionId, int pointsEarned, String feedback) {

    public CriterionScore {
        if (criterionId == null) {
            throw new IllegalArgumentException("criterionId is required");
        }
        if (pointsEarned < 0) {
            throw new IllegalArgumentException("pointsEarned cannot be negative");
        }
        feedback = feedback == null ? "" : feedback;
    }
}
