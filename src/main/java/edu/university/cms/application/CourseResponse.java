package edu.university.cms.application;

import edu.university.cms.domain.Course;

import java.util.UUID;

public record CourseResponse(
        UUID id,
        String title,
        UserResponse instructor,
        int rosterCount,
        int assignmentCount
) {

    public static CourseResponse from(Course course, int rosterCount, int assignmentCount) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                UserResponse.from(course.getInstructor()),
                rosterCount,
                assignmentCount
        );
    }
}
