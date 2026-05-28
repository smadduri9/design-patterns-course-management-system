package edu.university.cms.domain;

import java.util.Objects;
import java.util.UUID;

public record CourseEnrollment(
        UUID courseId,
        UUID studentId
) {

    public CourseEnrollment {
        Objects.requireNonNull(courseId, "courseId is required");
        Objects.requireNonNull(studentId, "studentId is required");
    }
}
