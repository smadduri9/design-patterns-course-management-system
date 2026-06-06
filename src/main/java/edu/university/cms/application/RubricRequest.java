package edu.university.cms.application;

import java.util.List;

public record RubricRequest(
        String title,
        List<RubricCriterionRequest> criteria
) {
}
