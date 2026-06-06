package edu.university.cms.application;

public record RubricCriterionRequest(
        String name,
        String description,
        int maxPoints
) {
}
