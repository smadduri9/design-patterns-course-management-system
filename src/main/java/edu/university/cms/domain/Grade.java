package edu.university.cms.domain;

public record Grade(int points, int maxPoints, String comments) {

    public Grade {
        if (maxPoints <= 0) {
            throw new IllegalArgumentException("maxPoints must be positive");
        }
        if (points < 0 || points > maxPoints) {
            throw new IllegalArgumentException("points must be between 0 and maxPoints");
        }
        comments = comments == null ? "" : comments;
    }
}
