package edu.university.cms.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Rubric {

    private final UUID id;
    private final String title;
    private final List<RubricCriterion> criteria;

    public Rubric(UUID id, String title, List<RubricCriterion> criteria) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.title = requireText(title, "title is required");
        this.criteria = new ArrayList<>(Objects.requireNonNull(criteria, "criteria are required"));
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<RubricCriterion> getCriteria() {
        return Collections.unmodifiableList(criteria);
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
