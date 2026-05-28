package edu.university.cms.application;

import edu.university.cms.domain.AIAnalysisReport;
import edu.university.cms.domain.Grade;
import edu.university.cms.domain.Notification;
import edu.university.cms.domain.Submission;
import edu.university.cms.domain.SubmissionStatus;
import edu.university.cms.patterns.behavioral.memento.FeedbackDraft;
import edu.university.cms.patterns.behavioral.memento.FeedbackDraftHistory;
import edu.university.cms.patterns.behavioral.observer.DomainEventPublisher;
import edu.university.cms.patterns.behavioral.observer.FeedbackFinalizedEvent;
import edu.university.cms.patterns.behavioral.observer.NotificationListener;
import edu.university.cms.repository.SubmissionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class InstructorReviewFacade {

    private final SubmissionRepository submissionRepository;
    private final DomainEventPublisher eventPublisher;
    private final NotificationListener notificationListener;
    private final PatternTraceService traceService;

    public InstructorReviewFacade(
            SubmissionRepository submissionRepository,
            DomainEventPublisher eventPublisher,
            NotificationListener notificationListener,
            PatternTraceService traceService
    ) {
        this.submissionRepository = submissionRepository;
        this.eventPublisher = eventPublisher;
        this.notificationListener = notificationListener;
        this.traceService = traceService;
    }

    public FeedbackDraft loadDraft(UUID submissionId) {
        Submission submission = findSubmission(submissionId);
        AIAnalysisReport report = submission.getReport()
                .orElseThrow(() -> new IllegalStateException("submission does not have an AI analysis report"));
        return new FeedbackDraft(report.getSuggestedFeedback(), traceService);
    }

    public InstructorReviewResult reviewAndFinalize(
            UUID submissionId,
            String firstEdit,
            String secondEdit,
            boolean restoreFirstEdit
    ) {
        Submission submission = findSubmission(submissionId);
        FeedbackDraft draft = loadDraft(submissionId);
        FeedbackDraftHistory history = new FeedbackDraftHistory(traceService);

        draft.edit(firstEdit);
        history.save(draft);
        draft.edit(secondEdit);
        history.save(draft);
        if (restoreFirstEdit) {
            draft.restore(history.restore(0));
        }

        Grade finalGrade = submission.getReport()
                .flatMap(AIAnalysisReport::getGradeSuggestion)
                .orElse(new Grade(0, 100, "No grade suggestion available."));
        submission.setFinalFeedback(draft.getFeedbackText());
        submission.setGrade(finalGrade);
        if (submission.getStatus() != SubmissionStatus.FINALIZED) {
            submission.finalizeSubmission(traceService);
        }
        submissionRepository.save(submission);

        eventPublisher.publish(new FeedbackFinalizedEvent(
                submission.getId(),
                submission.getStudent(),
                draft.getFeedbackText(),
                finalGrade,
                Instant.now()
        ));

        Notification notification = notificationListener.getLastNotification();
        return new InstructorReviewResult(submission, draft.getFeedbackText(), draft.getFeedbackText(), notification);
    }

    private Submission findSubmission(UUID submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("submission was not found"));
    }
}
