package edu.university.cms.patterns.behavioral.observer;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.OfficialPattern;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DomainEventPublisher {

    private final List<DomainEventListener<? extends DomainEvent>> listeners = new ArrayList<>();
    private final PatternTraceService traceService;

    public DomainEventPublisher(List<DomainEventListener<? extends DomainEvent>> listeners, PatternTraceService traceService) {
        this.listeners.addAll(listeners);
        this.traceService = traceService;
    }

    public void publish(DomainEvent event) {
        trace("Published " + event.getClass().getSimpleName() + " to registered listeners");
        for (DomainEventListener<? extends DomainEvent> listener : listeners) {
            if (listener.supports(event)) {
                notifyListener(listener, event);
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void notifyListener(DomainEventListener listener, DomainEvent event) {
        listener.onEvent(event);
    }

    private void trace(String description) {
        traceService.recordPhase5(
                OfficialPattern.OBSERVER,
                getClass().getSimpleName(),
                "Publish domain event",
                description,
                "Feedback finalization"
        );
    }
}
