package edu.university.cms.patterns.behavioral.state;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.Submission;
import edu.university.cms.domain.SubmissionStatus;

public class AnalyzingState extends AbstractSubmissionState {

    @Override
    public SubmissionStatus status() {
        return SubmissionStatus.ANALYZING;
    }

    @Override
    public void markAwaitingReview(Submission submission, PatternTraceService traceService) {
        submission.transitionTo(new AwaitingReviewState());
        trace(
                traceService,
                getClass().getSimpleName(),
                "Submission moved from Analyzing to AwaitingReview",
                "Placeholder analysis completed"
        );
    }
}
