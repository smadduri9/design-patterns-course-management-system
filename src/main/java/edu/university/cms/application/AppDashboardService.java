package edu.university.cms.application;

import edu.university.cms.domain.User;
import edu.university.cms.domain.UserRole;
import edu.university.cms.repository.AssignmentRepository;
import edu.university.cms.repository.CourseRepository;
import edu.university.cms.repository.SubmissionRepository;
import edu.university.cms.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AppDashboardService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final PatternTraceService traceService;

    public AppDashboardService(
            UserRepository userRepository,
            CourseRepository courseRepository,
            AssignmentRepository assignmentRepository,
            SubmissionRepository submissionRepository,
            PatternTraceService traceService
    ) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.traceService = traceService;
    }

    public DashboardResponse dashboard() {
        User instructor = instructor();
        int studentCount = (int) userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.STUDENT)
                .count();
        return new DashboardResponse(
                UserResponse.from(instructor),
                new DashboardCountsResponse(
                        courseRepository.findAll().size(),
                        studentCount,
                        assignmentRepository.findAll().size(),
                        submissionRepository.findAll().size(),
                        traceService.findAll().size()
                )
        );
    }

    private User instructor() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.INSTRUCTOR)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Seeded instructor was not found"));
    }
}
