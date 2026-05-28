package edu.university.cms.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Course {

    private final UUID id;
    private final String title;
    private final User instructor;
    private final List<CourseModule> modules;

    public Course(UUID id, String title, User instructor, List<CourseModule> modules) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.title = requireText(title, "title is required");
        this.instructor = Objects.requireNonNull(instructor, "instructor is required");
        if (instructor.getRole() != UserRole.INSTRUCTOR) {
            throw new IllegalArgumentException("course owner must be an instructor");
        }
        this.modules = new ArrayList<>(Objects.requireNonNull(modules, "modules are required"));
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public User getInstructor() {
        return instructor;
    }

    public List<CourseModule> getModules() {
        return Collections.unmodifiableList(modules);
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
