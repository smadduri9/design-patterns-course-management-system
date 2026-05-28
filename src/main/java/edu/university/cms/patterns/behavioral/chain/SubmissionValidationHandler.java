package edu.university.cms.patterns.behavioral.chain;

import edu.university.cms.application.PatternTraceService;

public interface SubmissionValidationHandler {

    SubmissionValidationHandler setNext(SubmissionValidationHandler next);

    void validate(SubmissionValidationContext context, PatternTraceService traceService);
}
