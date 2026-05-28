package edu.university.cms.patterns.behavioral.state;

import edu.university.cms.domain.SubmissionStatus;

public class FinalizedState extends AbstractSubmissionState {

    @Override
    public SubmissionStatus status() {
        return SubmissionStatus.FINALIZED;
    }
}
