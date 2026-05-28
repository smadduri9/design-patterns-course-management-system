package edu.university.cms.application;

import edu.university.cms.domain.Course;

import java.util.UUID;

public record CourseDetailResponse(
        UUID id,
        String title,
        UserResponse instructor,
        int rosterCount,
        int assignmentCount
) {

    public static CourseDetailResponse from(Course course, int rosterCount, int assignmentCount) {
        return new CourseDetailResponse(
                course.getId(),
                course.getTitle(),
                UserResponse.from(course.getInstructor()),
                rosterCount,
                assignmentCount
        );
    }
}
