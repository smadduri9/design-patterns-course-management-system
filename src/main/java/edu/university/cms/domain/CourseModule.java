package edu.university.cms.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CourseModule {

    private final UUID id;
    private final String title;
    private final List<Assignment> assignments;

    public CourseModule(UUID id, String title, List<Assignment> assignments) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.title = requireText(title, "title is required");
        this.assignments = new ArrayList<>(Objects.requireNonNull(assignments, "assignments are required"));
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<Assignment> getAssignments() {
        return Collections.unmodifiableList(assignments);
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
