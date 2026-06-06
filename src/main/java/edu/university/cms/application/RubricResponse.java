package edu.university.cms.application;

import edu.university.cms.domain.Rubric;

import java.util.List;
import java.util.UUID;

public record RubricResponse(
        UUID id,
        String title,
        List<RubricCriterionResponse> criteria
) {

    public static RubricResponse from(Rubric rubric) {
        return new RubricResponse(
                rubric.getId(),
                rubric.getTitle(),
                rubric.getCriteria().stream()
                        .map(RubricCriterionResponse::from)
                        .toList()
        );
    }
}
