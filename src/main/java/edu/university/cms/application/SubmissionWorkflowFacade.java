package edu.university.cms.application;

import edu.university.cms.domain.OfficialPattern;
import edu.university.cms.domain.SubmissionType;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SubmissionWorkflowFacade {

    private final SubmissionWorkflowMediator mediator;
    private final PatternTraceService traceService;

    public SubmissionWorkflowFacade(SubmissionWorkflowMediator mediator, PatternTraceService traceService) {
        this.mediator = mediator;
        this.traceService = traceService;
    }

    public SubmissionWorkflowResult submitAssignment(
            UUID studentId,
            UUID assignmentId,
            SubmissionType submissionType,
            String content
    ) {
        traceService.recordPhase3(
                OfficialPattern.FACADE,
                getClass().getSimpleName(),
                "Submit assignment",
                "Facade accepted a simple submission request and delegated workflow details to the mediator",
                "Controller submits assignment"
        );
        return mediator.submit(new SubmissionRequest(studentId, assignmentId, submissionType, content));
    }
}
