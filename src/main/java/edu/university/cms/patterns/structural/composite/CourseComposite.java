package edu.university.cms.patterns.structural.composite;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.Course;
import edu.university.cms.domain.OfficialPattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class CourseComposite implements CourseComponent {

    private final Course course;
    private final PatternTraceService traceService;
    private final List<CourseComponent> children = new ArrayList<>();

    public CourseComposite(Course course) {
        this(course, null);
    }

    public CourseComposite(Course course, PatternTraceService traceService) {
        this.course = Objects.requireNonNull(course, "course is required");
        this.traceService = traceService;
    }

    public void addModule(ModuleComposite module) {
        children.add(Objects.requireNonNull(module, "module is required"));
        trace("Instructor added a module to the course hierarchy");
    }

    @Override
    public UUID getId() {
        return course.getId();
    }

    @Override
    public String getTitle() {
        return course.getTitle();
    }

    @Override
    public String getComponentType() {
        return "Course";
    }

    @Override
    public List<CourseComponent> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public Course getCourse() {
        return course;
    }

    private void trace(String description) {
        Optional.ofNullable(traceService).ifPresent(service -> service.recordPhase2(
                OfficialPattern.COMPOSITE,
                getClass().getSimpleName(),
                "Create course content hierarchy",
                description,
                "Course contains modules"
        ));
    }
}
