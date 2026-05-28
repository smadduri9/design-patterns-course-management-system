package edu.university.cms.application;

import edu.university.cms.domain.Notification;
import edu.university.cms.domain.Submission;
import edu.university.cms.repository.NotificationRepository;
import edu.university.cms.repository.SubmissionRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.UUID;

@Service
public class StudentFeedbackService {

    private final SubmissionRepository submissionRepository;
    private final NotificationRepository notificationRepository;

    public StudentFeedbackService(SubmissionRepository submissionRepository, NotificationRepository notificationRepository) {
        this.submissionRepository = submissionRepository;
        this.notificationRepository = notificationRepository;
    }

    public StudentFeedbackView viewFeedback(UUID submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("submission was not found"));
        Notification notification = notificationRepository.findAll().stream()
                .filter(item -> item.getRecipient().getId().equals(submission.getStudent().getId()))
                .max(Comparator.comparing(Notification::getCreatedAt))
                .orElseThrow(() -> new IllegalStateException("notification was not found"));
        return new StudentFeedbackView(
                submission.getFinalFeedback().orElseThrow(() -> new IllegalStateException("final feedback is not available")),
                submission.getGrade().orElseThrow(() -> new IllegalStateException("grade is not available")),
                submission.getReport().orElseThrow(() -> new IllegalStateException("report is not available")).getSummary(),
                notification,
                submission.getReport().orElseThrow()
        );
    }
}
