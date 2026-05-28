package edu.university.cms.application;

import edu.university.cms.domain.Course;
import edu.university.cms.domain.User;
import edu.university.cms.domain.UserRole;
import edu.university.cms.repository.CourseAssignmentRepository;
import edu.university.cms.repository.CourseEnrollmentRepository;
import edu.university.cms.repository.CourseRepository;
import edu.university.cms.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class CourseAppService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final CourseAssignmentRepository courseAssignmentRepository;

    public CourseAppService(
            UserRepository userRepository,
            CourseRepository courseRepository,
            CourseEnrollmentRepository enrollmentRepository,
            CourseAssignmentRepository courseAssignmentRepository
    ) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseAssignmentRepository = courseAssignmentRepository;
    }

    public List<CourseResponse> listCourses() {
        return courseRepository.findAll().stream()
                .sorted(Comparator.comparing(Course::getTitle))
                .map(course -> CourseResponse.from(course, rosterCount(course), assignmentCount(course)))
                .toList();
    }

    public CourseResponse createCourse(CreateCourseRequest request) {
        if (request == null || request.title() == null || request.title().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "course title is required");
        }
        Course course = courseRepository.save(new Course(
                UUID.randomUUID(),
                request.title(),
                instructor(),
                List.of()
        ));
        return CourseResponse.from(course, 0, assignmentCount(course));
    }

    public CourseDetailResponse courseDetail(UUID courseId) {
        Course course = findCourse(courseId);
        return CourseDetailResponse.from(course, rosterCount(course), assignmentCount(course));
    }

    Course findCourse(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "course was not found"));
    }

    private User instructor() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.INSTRUCTOR)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Seeded instructor was not found"));
    }

    private int rosterCount(Course course) {
        return enrollmentRepository.countByCourseId(course.getId());
    }

    private int assignmentCount(Course course) {
        return courseAssignmentRepository.countByCourseId(course.getId());
    }
}
