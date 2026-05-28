package edu.university.cms.patterns.structural.bridge;

public class SubmissionReceivedNotification implements NotificationMessage {

    @Override
    public String subject() {
        return "Submission received";
    }

    @Override
    public String body() {
        return "Your submission has been received and is ready for analysis.";
    }
}
