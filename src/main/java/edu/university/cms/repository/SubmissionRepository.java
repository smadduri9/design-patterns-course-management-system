package edu.university.cms.repository;

import edu.university.cms.domain.Submission;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class SubmissionRepository {

    private final ConcurrentMap<UUID, Submission> submissions = new ConcurrentHashMap<>();

    public Submission save(Submission submission) {
        submissions.put(submission.getId(), submission);
        return submission;
    }

    public Optional<Submission> findById(UUID id) {
        return Optional.ofNullable(submissions.get(id));
    }

    public List<Submission> findAll() {
        return new ArrayList<>(submissions.values());
    }
}
