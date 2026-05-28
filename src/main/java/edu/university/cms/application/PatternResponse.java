package edu.university.cms.application;

import edu.university.cms.domain.OfficialPattern;

public record PatternResponse(
        String key,
        String displayName,
        String category
) {

    public static PatternResponse from(OfficialPattern pattern) {
        return new PatternResponse(pattern.name(), pattern.getDisplayName(), pattern.getCategory().name());
    }
}
