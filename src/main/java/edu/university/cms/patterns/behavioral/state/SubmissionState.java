package edu.university.cms.patterns.behavioral.state;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.Submission;
import edu.university.cms.domain.SubmissionStatus;

public interface SubmissionState {

    SubmissionStatus status();

    default void submit(Submission submission, PatternTraceService traceService) {
        throw new IllegalStateException("Cannot submit from " + status());
    }

    default void startAnalysis(Submission submission, PatternTraceService traceService) {
        throw new IllegalStateException("Cannot start analysis from " + status());
    }

    default void markAwaitingReview(Submission submission, PatternTraceService traceService) {
        throw new IllegalStateException("Cannot await review from " + status());
    }

    default void finalizeSubmission(Submission submission, PatternTraceService traceService) {
        throw new IllegalStateException("Cannot finalize from " + status());
    }
}
