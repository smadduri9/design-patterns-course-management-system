package edu.university.cms.application;

import edu.university.cms.domain.CourseEnrollment;
import edu.university.cms.domain.User;
import edu.university.cms.domain.UserRole;
import edu.university.cms.repository.CourseEnrollmentRepository;
import edu.university.cms.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class RosterAppService {

    private final CourseAppService courseAppService;
    private final UserRepository userRepository;
    private final CourseEnrollmentRepository enrollmentRepository;

    public RosterAppService(
            CourseAppService courseAppService,
            UserRepository userRepository,
            CourseEnrollmentRepository enrollmentRepository
    ) {
        this.courseAppService = courseAppService;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public RosterResponse roster(UUID courseId) {
        courseAppService.findCourse(courseId);
        return new RosterResponse(courseId, enrolledStudents(courseId));
    }

    public RosterResponse enroll(UUID courseId, EnrollmentRequest request) {
        courseAppService.findCourse(courseId);
        if (request == null || request.studentIds() == null || request.studentIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "at least one studentId is required");
        }
        for (UUID studentId : request.studentIds()) {
            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student was not found"));
            if (student.getRole() != UserRole.STUDENT) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "only students can be enrolled");
            }
            enrollmentRepository.save(new CourseEnrollment(courseId, studentId));
        }
        return roster(courseId);
    }

    private List<UserResponse> enrolledStudents(UUID courseId) {
        return enrollmentRepository.findStudentIdsByCourseId(courseId).stream()
                .map(studentId -> userRepository.findById(studentId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student was not found")))
                .sorted(Comparator.comparing(User::getName))
                .map(UserResponse::from)
                .toList();
    }
}
