package edu.university.cms.patterns.behavioral.chain;

import java.time.LocalDate;

public class AssignmentOpenValidationHandler extends AbstractSubmissionValidationHandler {

    @Override
    protected void doValidate(SubmissionValidationContext context) {
        if (context.assignment().getDueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("assignment is closed");
        }
    }

    @Override
    protected String description() {
        return "Validated that the assignment is still open for submissions";
    }
}
