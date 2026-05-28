package edu.university.cms.patterns.behavioral.chain;

public class SubmissionTypeValidationHandler extends AbstractSubmissionValidationHandler {

    @Override
    protected void doValidate(SubmissionValidationContext context) {
        if (context.submissionType() == null) {
            throw new IllegalArgumentException("submission type is required");
        }
        if (!context.assignment().getAcceptedSubmissionTypes().contains(context.submissionType())) {
            throw new IllegalArgumentException("assignment does not accept this submission type");
        }
    }

    @Override
    protected String description() {
        return "Validated that the assignment accepts the submitted content type";
    }
}
