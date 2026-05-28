package edu.university.cms.patterns.structural.composite;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.CourseModule;
import edu.university.cms.domain.OfficialPattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ModuleComposite implements CourseComponent {

    private final CourseModule module;
    private final PatternTraceService traceService;
    private final List<CourseComponent> children = new ArrayList<>();

    public ModuleComposite(CourseModule module) {
        this(module, null);
    }

    public ModuleComposite(CourseModule module, PatternTraceService traceService) {
        this.module = Objects.requireNonNull(module, "module is required");
        this.traceService = traceService;
    }

    public void addAssignment(AssignmentLeaf assignment) {
        children.add(Objects.requireNonNull(assignment, "assignment is required"));
        trace("Instructor added an assignment to a module");
    }

    @Override
    public UUID getId() {
        return module.getId();
    }

    @Override
    public String getTitle() {
        return module.getTitle();
    }

    @Override
    public String getComponentType() {
        return "Module";
    }

    @Override
    public List<CourseComponent> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public CourseModule getModule() {
        return module;
    }

    private void trace(String description) {
        Optional.ofNullable(traceService).ifPresent(service -> service.recordPhase2(
                OfficialPattern.COMPOSITE,
                getClass().getSimpleName(),
                "Create course content hierarchy",
                description,
                "Module contains assignments"
        ));
    }
}
