package edu.university.cms.patterns.behavioral.chain;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.OfficialPattern;

abstract class AbstractSubmissionValidationHandler implements SubmissionValidationHandler {

    private SubmissionValidationHandler next;

    @Override
    public SubmissionValidationHandler setNext(SubmissionValidationHandler next) {
        this.next = next;
        return next;
    }

    @Override
    public final void validate(SubmissionValidationContext context, PatternTraceService traceService) {
        doValidate(context);
        trace(traceService);
        if (next != null) {
            next.validate(context, traceService);
        }
    }

    protected abstract void doValidate(SubmissionValidationContext context);

    protected abstract String description();

    private void trace(PatternTraceService traceService) {
        if (traceService != null) {
            traceService.recordPhase3(
                    OfficialPattern.CHAIN_OF_RESPONSIBILITY,
                    getClass().getSimpleName(),
                    "Validate submission",
                    description(),
                    "Submission validation chain"
            );
        }
    }
}
