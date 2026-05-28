package edu.university.cms.patterns.behavioral.observer;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.Notification;
import edu.university.cms.domain.OfficialPattern;
import edu.university.cms.patterns.structural.bridge.FeedbackPublishedNotification;
import edu.university.cms.patterns.structural.bridge.InAppNotificationSender;
import edu.university.cms.patterns.structural.bridge.NotificationMessage;
import edu.university.cms.patterns.structural.bridge.NotificationSender;
import edu.university.cms.repository.NotificationRepository;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener implements DomainEventListener<FeedbackFinalizedEvent> {

    private final NotificationRepository notificationRepository;
    private final PatternTraceService traceService;
    private Notification lastNotification;

    public NotificationListener(NotificationRepository notificationRepository, PatternTraceService traceService) {
        this.notificationRepository = notificationRepository;
        this.traceService = traceService;
    }

    @Override
    public boolean supports(DomainEvent event) {
        return event instanceof FeedbackFinalizedEvent;
    }

    @Override
    public void onEvent(FeedbackFinalizedEvent event) {
        trace("Notification listener reacted to feedback finalization event");
        NotificationMessage message = new FeedbackPublishedNotification(event.finalFeedback(), event.grade());
        NotificationSender sender = new InAppNotificationSender(traceService);
        lastNotification = notificationRepository.save(sender.send(event.student(), message));
    }

    public Notification getLastNotification() {
        return lastNotification;
    }

    private void trace(String description) {
        traceService.recordPhase5(
                OfficialPattern.OBSERVER,
                getClass().getSimpleName(),
                "Handle domain event",
                description,
                "Feedback finalization"
        );
    }
}
