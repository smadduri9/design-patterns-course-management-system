package edu.university.cms.patterns.behavioral.observer;

public interface DomainEventListener<T extends DomainEvent> {

    boolean supports(DomainEvent event);

    void onEvent(T event);
}
