package edu.university.cms.patterns.behavioral.memento;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.OfficialPattern;

import java.time.Instant;
import java.util.Objects;

public class FeedbackDraft {

    private final PatternTraceService traceService;
    private String feedbackText;

    public FeedbackDraft(String feedbackText, PatternTraceService traceService) {
        this.feedbackText = requireText(feedbackText, "feedbackText is required");
        this.traceService = traceService;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void edit(String feedbackText) {
        this.feedbackText = requireText(feedbackText, "feedbackText is required");
        trace("Instructor edited feedback draft text");
    }

    public FeedbackDraftMemento save() {
        trace("Instructor saved a feedback draft snapshot");
        return new FeedbackDraftMemento(feedbackText, Instant.now());
    }

    public void restore(FeedbackDraftMemento memento) {
        this.feedbackText = Objects.requireNonNull(memento, "memento is required").feedbackText();
        trace("Instructor restored feedback draft from a saved snapshot");
    }

    private void trace(String description) {
        if (traceService != null) {
            traceService.recordPhase5(
                    OfficialPattern.MEMENTO,
                    getClass().getSimpleName(),
                    "Edit instructor feedback",
                    description,
                    "Instructor review"
            );
        }
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
