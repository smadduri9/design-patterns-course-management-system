package edu.university.cms.domain;

import java.util.Objects;
import java.util.UUID;

public class User {

    private final UUID id;
    private final String name;
    private final UserRole role;

    public User(UUID id, String name, UserRole role) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.name = requireText(name, "name is required");
        this.role = Objects.requireNonNull(role, "role is required");
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UserRole getRole() {
        return role;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
