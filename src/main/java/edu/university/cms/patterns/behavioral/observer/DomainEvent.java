package edu.university.cms.patterns.behavioral.observer;

import java.time.Instant;

public interface DomainEvent {

    Instant occurredAt();
}
