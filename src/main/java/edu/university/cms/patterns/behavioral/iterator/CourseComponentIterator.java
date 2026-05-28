package edu.university.cms.patterns.behavioral.iterator;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.OfficialPattern;
import edu.university.cms.patterns.structural.composite.CourseComponent;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class CourseComponentIterator implements Iterator<CourseComponent> {

    private final Deque<CourseComponent> stack = new ArrayDeque<>();

    public CourseComponentIterator(CourseComponent root) {
        this(root, null);
    }

    public CourseComponentIterator(CourseComponent root, PatternTraceService traceService) {
        stack.push(Objects.requireNonNull(root, "root is required"));
        if (traceService != null) {
            traceService.recordPhase2(
                    OfficialPattern.ITERATOR,
                    getClass().getSimpleName(),
                    "Traverse course hierarchy",
                    "Iterator prepared a course/module/assignment traversal",
                    "Render course content"
            );
        }
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public CourseComponent next() {
        CourseComponent current = stack.pop();
        List<CourseComponent> children = current.getChildren();
        for (int index = children.size() - 1; index >= 0; index--) {
            stack.push(children.get(index));
        }
        return current;
    }
}
