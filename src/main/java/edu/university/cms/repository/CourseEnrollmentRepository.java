package edu.university.cms.repository;

import edu.university.cms.domain.CourseEnrollment;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Repository
public class CourseEnrollmentRepository {

    private final Map<UUID, Set<UUID>> studentIdsByCourseId = new HashMap<>();

    public synchronized CourseEnrollment save(CourseEnrollment enrollment) {
        studentIdsByCourseId
                .computeIfAbsent(enrollment.courseId(), ignored -> new HashSet<>())
                .add(enrollment.studentId());
        return enrollment;
    }

    public synchronized List<UUID> findStudentIdsByCourseId(UUID courseId) {
        return studentIdsByCourseId.getOrDefault(courseId, Set.of()).stream()
                .sorted()
                .toList();
    }

    public synchronized int countByCourseId(UUID courseId) {
        return studentIdsByCourseId.getOrDefault(courseId, Set.of()).size();
    }
}
