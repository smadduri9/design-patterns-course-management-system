package edu.university.cms.patterns.behavioral.state;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.Submission;
import edu.university.cms.domain.SubmissionStatus;

public class SubmittedState extends AbstractSubmissionState {

    @Override
    public SubmissionStatus status() {
        return SubmissionStatus.SUBMITTED;
    }

    @Override
    public void startAnalysis(Submission submission, PatternTraceService traceService) {
        submission.transitionTo(new AnalyzingState());
        trace(
                traceService,
                getClass().getSimpleName(),
                "Submission moved from Submitted to Analyzing",
                "System begins submission workflow"
        );
    }
}
