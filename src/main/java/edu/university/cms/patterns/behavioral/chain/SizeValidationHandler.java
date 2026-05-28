package edu.university.cms.patterns.behavioral.chain;

public class SizeValidationHandler extends AbstractSubmissionValidationHandler {

    private final int maxCharacters;

    public SizeValidationHandler() {
        this(10_000);
    }

    public SizeValidationHandler(int maxCharacters) {
        this.maxCharacters = maxCharacters;
    }

    @Override
    protected void doValidate(SubmissionValidationContext context) {
        String content = context.content();
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("submission content is required");
        }
        if (content.length() > maxCharacters) {
            throw new IllegalArgumentException("submission content exceeds maximum size");
        }
    }

    @Override
    protected String description() {
        return "Validated that submission content is present and within the allowed size";
    }
}
