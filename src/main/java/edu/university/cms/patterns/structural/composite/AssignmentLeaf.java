package edu.university.cms.patterns.structural.composite;

import edu.university.cms.domain.Assignment;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AssignmentLeaf implements CourseComponent {

    private final Assignment assignment;

    public AssignmentLeaf(Assignment assignment) {
        this.assignment = Objects.requireNonNull(assignment, "assignment is required");
    }

    @Override
    public UUID getId() {
        return assignment.getId();
    }

    @Override
    public String getTitle() {
        return assignment.getTitle();
    }

    @Override
    public String getComponentType() {
        return "Assignment";
    }

    @Override
    public List<CourseComponent> getChildren() {
        return List.of();
    }

    public Assignment getAssignment() {
        return assignment;
    }
}
