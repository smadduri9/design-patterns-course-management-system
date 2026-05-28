package edu.university.cms.application;

import java.util.List;
import java.util.UUID;

public record RosterResponse(
        UUID courseId,
        List<UserResponse> students
) {
}
