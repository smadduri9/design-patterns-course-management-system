package edu.university.cms.repository;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.patterns.behavioral.memento.FeedbackDraft;
import edu.university.cms.patterns.behavioral.memento.FeedbackDraftHistory;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class FeedbackDraftRepository {

    private final ConcurrentMap<UUID, FeedbackDraftSession> sessions = new ConcurrentHashMap<>();

    public FeedbackDraftSession findOrCreate(UUID submissionId, String initialFeedback, PatternTraceService traceService) {
        return sessions.computeIfAbsent(
                submissionId,
                ignored -> new FeedbackDraftSession(
                        new FeedbackDraft(initialFeedback, traceService),
                        new FeedbackDraftHistory(traceService)
                )
        );
    }

    public Optional<FeedbackDraftSession> findBySubmissionId(UUID submissionId) {
        return Optional.ofNullable(sessions.get(submissionId));
    }

    public record FeedbackDraftSession(
            FeedbackDraft draft,
            FeedbackDraftHistory history
    ) {
    }
}
