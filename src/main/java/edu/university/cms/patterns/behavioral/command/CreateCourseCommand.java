package edu.university.cms.patterns.behavioral.command;

import edu.university.cms.domain.Course;
import edu.university.cms.repository.CourseRepository;

import java.util.Objects;

public class CreateCourseCommand implements CourseCommand<Course> {

    private final CourseRepository courseRepository;
    private final Course course;

    public CreateCourseCommand(CourseRepository courseRepository, Course course) {
        this.courseRepository = Objects.requireNonNull(courseRepository, "courseRepository is required");
        this.course = Objects.requireNonNull(course, "course is required");
    }

    @Override
    public String name() {
        return "Create course";
    }

    @Override
    public Course execute() {
        return courseRepository.save(course);
    }
}
