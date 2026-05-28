package edu.university.cms.patterns.structural.bridge;

import edu.university.cms.domain.Grade;

public class FeedbackPublishedNotification implements NotificationMessage {

    private final String feedback;
    private final Grade grade;

    public FeedbackPublishedNotification(String feedback, Grade grade) {
        this.feedback = feedback;
        this.grade = grade;
    }

    @Override
    public String subject() {
        return "Final feedback is available";
    }

    @Override
    public String body() {
        return "Final feedback: " + feedback + " Grade: " + grade.points() + "/" + grade.maxPoints();
    }
}
