package edu.university.cms.patterns.behavioral.observer;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.OfficialPattern;
import org.springframework.stereotype.Component;

@Component
public class PatternTraceListener implements DomainEventListener<FeedbackFinalizedEvent> {

    private final PatternTraceService traceService;

    public PatternTraceListener(PatternTraceService traceService) {
        this.traceService = traceService;
    }

    @Override
    public boolean supports(DomainEvent event) {
        return event instanceof FeedbackFinalizedEvent;
    }

    @Override
    public void onEvent(FeedbackFinalizedEvent event) {
        traceService.recordPhase5(
                OfficialPattern.OBSERVER,
                getClass().getSimpleName(),
                "Handle domain event",
                "Pattern trace listener observed feedback finalization",
                "Feedback finalization"
        );
    }
}
