package edu.university.cms.application;

import edu.university.cms.domain.RubricCriterion;

import java.util.UUID;

public record RubricCriterionResponse(
        UUID id,
        String name,
        String description,
        int maxPoints
) {

    public static RubricCriterionResponse from(RubricCriterion criterion) {
        return new RubricCriterionResponse(
                criterion.getId(),
                criterion.getName(),
                criterion.getDescription(),
                criterion.getMaxPoints()
        );
    }
}
