package edu.university.cms.patterns.structural.bridge;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.domain.Notification;
import edu.university.cms.domain.OfficialPattern;
import edu.university.cms.domain.User;

import java.time.Instant;
import java.util.UUID;

public class InAppNotificationSender implements NotificationSender {

    private final PatternTraceService traceService;

    public InAppNotificationSender(PatternTraceService traceService) {
        this.traceService = traceService;
    }

    @Override
    public Notification send(User recipient, NotificationMessage message) {
        trace("Sent notification message through in-app channel");
        return new Notification(UUID.randomUUID(), recipient, message.subject() + " - " + message.body(), Instant.now());
    }

    private void trace(String description) {
        if (traceService != null) {
            traceService.recordPhase5(
                    OfficialPattern.BRIDGE,
                    getClass().getSimpleName(),
                    "Send notification",
                    description,
                    "Feedback finalization"
            );
        }
    }
}
