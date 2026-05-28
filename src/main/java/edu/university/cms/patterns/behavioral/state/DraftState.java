package edu.university.cms.patterns.behavioral.state;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.Submission;
import edu.university.cms.domain.SubmissionStatus;

public class DraftState extends AbstractSubmissionState {

    @Override
    public SubmissionStatus status() {
        return SubmissionStatus.DRAFT;
    }

    @Override
    public void submit(Submission submission, PatternTraceService traceService) {
        submission.transitionTo(new SubmittedState());
        trace(
                traceService,
                getClass().getSimpleName(),
                "Submission moved from Draft to Submitted",
                "Student submits assignment"
        );
    }
}
