package edu.university.cms.application;

import edu.university.cms.domain.Grade;

public record GradeResponse(
        int points,
        int maxPoints,
        String explanation
) {

    public static GradeResponse from(Grade grade) {
        return new GradeResponse(grade.points(), grade.maxPoints(), grade.comments());
    }
}
