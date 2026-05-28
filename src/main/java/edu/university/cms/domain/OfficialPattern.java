package edu.university.cms.domain;

import java.util.Arrays;
import java.util.Optional;

public enum OfficialPattern {
    FACTORY_METHOD("Factory Method", PatternCategory.CREATIONAL),
    ABSTRACT_FACTORY("Abstract Factory", PatternCategory.CREATIONAL),
    BUILDER("Builder", PatternCategory.CREATIONAL),
    COMPOSITE("Composite", PatternCategory.STRUCTURAL),
    ADAPTER("Adapter", PatternCategory.STRUCTURAL),
    FACADE("Facade", PatternCategory.STRUCTURAL),
    DECORATOR("Decorator", PatternCategory.STRUCTURAL),
    PROXY("Proxy", PatternCategory.STRUCTURAL),
    BRIDGE("Bridge", PatternCategory.STRUCTURAL),
    STATE("State", PatternCategory.BEHAVIORAL),
    STRATEGY("Strategy", PatternCategory.BEHAVIORAL),
    OBSERVER("Observer", PatternCategory.BEHAVIORAL),
    COMMAND("Command", PatternCategory.BEHAVIORAL),
    CHAIN_OF_RESPONSIBILITY("Chain of Responsibility", PatternCategory.BEHAVIORAL),
    MEDIATOR("Mediator", PatternCategory.BEHAVIORAL),
    TEMPLATE_METHOD("Template Method", PatternCategory.BEHAVIORAL),
    MEMENTO("Memento", PatternCategory.BEHAVIORAL),
    ITERATOR("Iterator", PatternCategory.BEHAVIORAL);

    private final String displayName;
    private final PatternCategory category;

    OfficialPattern(String displayName, PatternCategory category) {
        this.displayName = displayName;
        this.category = category;
    }

    public String getDisplayName() {
        return displayName;
    }

    public PatternCategory getCategory() {
        return category;
    }

    public static Optional<OfficialPattern> fromDisplayName(String displayName) {
        return Arrays.stream(values())
                .filter(pattern -> pattern.displayName.equals(displayName))
                .findFirst();
    }
}
