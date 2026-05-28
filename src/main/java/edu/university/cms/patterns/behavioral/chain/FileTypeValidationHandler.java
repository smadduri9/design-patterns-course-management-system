package edu.university.cms.patterns.behavioral.chain;

import edu.university.cms.domain.SubmissionType;

public class FileTypeValidationHandler extends AbstractSubmissionValidationHandler {

    @Override
    protected void doValidate(SubmissionValidationContext context) {
        if (context.submissionType() == SubmissionType.JAVA_CODE && !looksLikeJava(context.content())) {
            throw new IllegalArgumentException("Java code submissions must contain Java source text");
        }
        if (context.submissionType() == SubmissionType.PDF_TEXT && context.content().contains("public class")) {
            throw new IllegalArgumentException("PDF/text submissions should contain prose content");
        }
    }

    @Override
    protected String description() {
        return "Validated content shape for the selected submission type";
    }

    private boolean looksLikeJava(String content) {
        return content.contains("class ") || content.contains("interface ") || content.contains("record ");
    }
}
