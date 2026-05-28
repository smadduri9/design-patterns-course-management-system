package edu.university.cms.patterns.behavioral.state;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.Submission;
import edu.university.cms.domain.SubmissionStatus;

public class AwaitingReviewState extends AbstractSubmissionState {

    @Override
    public SubmissionStatus status() {
        return SubmissionStatus.AWAITING_REVIEW;
    }

    @Override
    public void finalizeSubmission(Submission submission, PatternTraceService traceService) {
        submission.transitionTo(new FinalizedState());
        trace(
                traceService,
                getClass().getSimpleName(),
                "Submission moved from AwaitingReview to Finalized",
                "Instructor finalizes feedback"
        );
    }
}
