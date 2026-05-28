package edu.university.cms.domain;

import java.util.Objects;
import java.util.UUID;

public class RubricCriterion {

    private final UUID id;
    private final String name;
    private final String description;
    private final int maxPoints;

    public RubricCriterion(UUID id, String name, String description, int maxPoints) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.name = requireText(name, "name is required");
        this.description = requireText(description, "description is required");
        if (maxPoints <= 0) {
            throw new IllegalArgumentException("maxPoints must be positive");
        }
        this.maxPoints = maxPoints;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
