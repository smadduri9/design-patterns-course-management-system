package edu.university.cms.repository;

import edu.university.cms.domain.Notification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class NotificationRepository {

    private final ConcurrentMap<UUID, Notification> notifications = new ConcurrentHashMap<>();

    public Notification save(Notification notification) {
        notifications.put(notification.getId(), notification);
        return notification;
    }

    public Optional<Notification> findById(UUID id) {
        return Optional.ofNullable(notifications.get(id));
    }

    public List<Notification> findAll() {
        return new ArrayList<>(notifications.values());
    }
}
