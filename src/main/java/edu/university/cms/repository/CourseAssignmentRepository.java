package edu.university.cms.repository;

import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class CourseAssignmentRepository {

    private final ConcurrentMap<UUID, Set<UUID>> assignmentIdsByCourseId = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, UUID> courseIdByAssignmentId = new ConcurrentHashMap<>();

    public synchronized void link(UUID courseId, UUID assignmentId) {
        assignmentIdsByCourseId
                .computeIfAbsent(courseId, ignored -> new LinkedHashSet<>())
                .add(assignmentId);
        courseIdByAssignmentId.put(assignmentId, courseId);
    }

    public synchronized List<UUID> findAssignmentIdsByCourseId(UUID courseId) {
        return assignmentIdsByCourseId.getOrDefault(courseId, Set.of()).stream()
                .toList();
    }

    public Optional<UUID> findCourseIdByAssignmentId(UUID assignmentId) {
        return Optional.ofNullable(courseIdByAssignmentId.get(assignmentId));
    }

    public synchronized int countByCourseId(UUID courseId) {
        return assignmentIdsByCourseId.getOrDefault(courseId, Set.of()).size();
    }
}
