package edu.university.cms.patterns.structural.bridge;

import edu.university.cms.domain.Notification;
import edu.university.cms.domain.User;

public interface NotificationSender {

    Notification send(User recipient, NotificationMessage message);
}
