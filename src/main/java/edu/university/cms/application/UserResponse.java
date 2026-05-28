package edu.university.cms.application;

import edu.university.cms.domain.User;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String role
) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getRole().name());
    }
}
