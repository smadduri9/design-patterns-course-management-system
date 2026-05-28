package edu.university.cms.patterns.behavioral.command;

import edu.university.cms.domain.Assignment;
import edu.university.cms.patterns.structural.composite.AssignmentLeaf;
import edu.university.cms.patterns.structural.composite.ModuleComposite;
import edu.university.cms.repository.AssignmentRepository;

import java.util.Objects;

public class CreateAssignmentCommand implements CourseCommand<Assignment> {

    private final AssignmentRepository assignmentRepository;
    private final ModuleComposite module;
    private final Assignment assignment;

    public CreateAssignmentCommand(
            AssignmentRepository assignmentRepository,
            ModuleComposite module,
            Assignment assignment
    ) {
        this.assignmentRepository = Objects.requireNonNull(assignmentRepository, "assignmentRepository is required");
        this.module = Objects.requireNonNull(module, "module is required");
        this.assignment = Objects.requireNonNull(assignment, "assignment is required");
    }

    @Override
    public String name() {
        return "Create assignment";
    }

    @Override
    public Assignment execute() {
        Assignment savedAssignment = assignmentRepository.save(assignment);
        module.addAssignment(new AssignmentLeaf(savedAssignment));
        return savedAssignment;
    }
}
