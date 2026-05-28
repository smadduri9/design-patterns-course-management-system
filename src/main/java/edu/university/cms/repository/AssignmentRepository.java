package edu.university.cms.repository;

import edu.university.cms.domain.Assignment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class AssignmentRepository {

    private final ConcurrentMap<UUID, Assignment> assignments = new ConcurrentHashMap<>();

    public Assignment save(Assignment assignment) {
        assignments.put(assignment.getId(), assignment);
        return assignment;
    }

    public Optional<Assignment> findById(UUID id) {
        return Optional.ofNullable(assignments.get(id));
    }

    public List<Assignment> findAll() {
        return new ArrayList<>(assignments.values());
    }
}
