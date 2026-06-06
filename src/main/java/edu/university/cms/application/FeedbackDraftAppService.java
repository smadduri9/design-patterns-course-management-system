package edu.university.cms.application;

import edu.university.cms.domain.AIAnalysisReport;
import edu.university.cms.domain.Grade;
import edu.university.cms.domain.Notification;
import edu.university.cms.domain.Submission;
import edu.university.cms.domain.SubmissionStatus;
import edu.university.cms.patterns.behavioral.memento.FeedbackDraftMemento;
import edu.university.cms.patterns.behavioral.observer.DomainEventPublisher;
import edu.university.cms.patterns.behavioral.observer.FeedbackFinalizedEvent;
import edu.university.cms.repository.FeedbackDraftRepository;
import edu.university.cms.repository.NotificationRepository;
import edu.university.cms.repository.SubmissionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
public class FeedbackDraftAppService {

    private final SubmissionRepository submissionRepository;
    private final FeedbackDraftRepository feedbackDraftRepository;
    private final DomainEventPublisher eventPublisher;
    private final NotificationRepository notificationRepository;
    private final PatternTraceService traceService;

    public FeedbackDraftAppService(
            SubmissionRepository submissionRepository,
            FeedbackDraftRepository feedbackDraftRepository,
            DomainEventPublisher eventPublisher,
            NotificationRepository notificationRepository,
            PatternTraceService traceService
    ) {
        this.submissionRepository = submissionRepository;
        this.feedbackDraftRepository = feedbackDraftRepository;
        this.eventPublisher = eventPublisher;
        this.notificationRepository = notificationRepository;
        this.traceService = traceService;
    }

    public FeedbackDraftResponse drafts(UUID submissionId) {
        Submission submission = analyzedSubmission(submissionId);
        return response(submission.getId(), sessionFor(submission));
    }

    public FeedbackDraftResponse saveDraft(UUID submissionId, SaveFeedbackDraftRequest request) {
        Submission submission = analyzedSubmission(submissionId);
        if (request == null || request.feedbackText() == null || request.feedbackText().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "feedbackText is required");
        }
        FeedbackDraftRepository.FeedbackDraftSession session = sessionFor(submission);
        try {
            session.draft().edit(request.feedbackText());
            session.history().save(session.draft());
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
        return response(submission.getId(), session);
    }

    public FeedbackDraftResponse restoreDraft(UUID submissionId, RestoreFeedbackDraftRequest request) {
        Submission submission = analyzedSubmission(submissionId);
        if (request == null || request.draftIndex() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "valid draftIndex is required");
        }
        FeedbackDraftRepository.FeedbackDraftSession session = sessionFor(submission);
        try {
            session.draft().restore(session.history().restore(request.draftIndex()));
        } catch (IndexOutOfBoundsException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "draftIndex was not found", exception);
        }
        return response(submission.getId(), session);
    }

    public FinalFeedbackResponse finalizeFeedback(UUID submissionId, FinalizeFeedbackRequest request) {
        Submission submission = analyzedSubmission(submissionId);
        if (request == null || request.feedbackText() == null || request.feedbackText().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "feedbackText is required");
        }
        Grade finalGrade = submission.getReport()
                .flatMap(AIAnalysisReport::getGradeSuggestion)
                .orElse(new Grade(0, 100, "No grade suggestion available."));
        submission.setFinalFeedback(request.feedbackText());
        submission.setGrade(finalGrade);
        if (submission.getStatus() != SubmissionStatus.FINALIZED) {
            submission.finalizeSubmission(traceService);
        }
        submissionRepository.save(submission);

        eventPublisher.publish(new FeedbackFinalizedEvent(
                submission.getId(),
                submission.getStudent(),
                request.feedbackText(),
                finalGrade,
                Instant.now()
        ));

        Notification notification = latestNotificationFor(submission);
        return new FinalFeedbackResponse(
                submission.getId(),
                submission.getStatus().name(),
                request.feedbackText(),
                GradeResponse.from(finalGrade),
                NotificationResponse.from(notification)
        );
    }

    public StudentFeedbackResponse studentFeedback(UUID submissionId) {
        Submission submission = findSubmission(submissionId);
        if (submission.getStatus() != SubmissionStatus.FINALIZED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "final feedback is not available");
        }
        AIAnalysisReport report = submission.getReport()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "report is not available"));
        return new StudentFeedbackResponse(
                submission.getId(),
                submission.getFinalFeedback()
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "final feedback is not available")),
                submission.getGrade()
                        .map(GradeResponse::from)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "grade is not available")),
                report.getSummary(),
                NotificationResponse.from(latestNotificationFor(submission)),
                AIAnalysisReportResponse.from(report)
        );
    }

    private FeedbackDraftRepository.FeedbackDraftSession sessionFor(Submission submission) {
        return feedbackDraftRepository.findOrCreate(
                submission.getId(),
                submission.getReport().orElseThrow().getSuggestedFeedback(),
                traceService
        );
    }

    private FeedbackDraftResponse response(UUID submissionId, FeedbackDraftRepository.FeedbackDraftSession session) {
        List<FeedbackDraftMemento> snapshots = session.history().findAll();
        return new FeedbackDraftResponse(
                submissionId,
                session.draft().getFeedbackText(),
                IntStream.range(0, snapshots.size())
                        .mapToObj(index -> FeedbackDraftSnapshotResponse.from(index, snapshots.get(index)))
                        .toList()
        );
    }

    private Submission analyzedSubmission(UUID submissionId) {
        Submission submission = findSubmission(submissionId);
        if (submission.getReport().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "submission does not have an AI analysis report");
        }
        return submission;
    }

    private Submission findSubmission(UUID submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "submission was not found"));
    }

    private Notification latestNotificationFor(Submission submission) {
        return notificationRepository.findAll().stream()
                .filter(notification -> notification.getRecipient().getId().equals(submission.getStudent().getId()))
                .max(Comparator.comparing(Notification::getCreatedAt))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "notification was not found"));
    }
}
