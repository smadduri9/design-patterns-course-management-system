package edu.university.cms.application;

import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.GradingStrategyType;
import edu.university.cms.domain.Rubric;
import edu.university.cms.domain.RubricCriterion;
import edu.university.cms.domain.SubmissionType;
import edu.university.cms.patterns.creational.builder.AssignmentBuilder;
import edu.university.cms.repository.AssignmentRepository;
import edu.university.cms.repository.CourseAssignmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AssignmentAppService {

    private final CourseAppService courseAppService;
    private final AssignmentRepository assignmentRepository;
    private final CourseAssignmentRepository courseAssignmentRepository;

    public AssignmentAppService(
            CourseAppService courseAppService,
            AssignmentRepository assignmentRepository,
            CourseAssignmentRepository courseAssignmentRepository
    ) {
        this.courseAppService = courseAppService;
        this.assignmentRepository = assignmentRepository;
        this.courseAssignmentRepository = courseAssignmentRepository;
    }

    public List<AssignmentResponse> listAssignments(UUID courseId) {
        courseAppService.findCourse(courseId);
        return courseAssignmentRepository.findAssignmentIdsByCourseId(courseId).stream()
                .map(assignmentId -> assignmentRepository.findById(assignmentId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "assignment was not found")))
                .map(assignment -> AssignmentResponse.from(assignment, courseId))
                .toList();
    }

    public AssignmentResponse createAssignment(UUID courseId, CreateAssignmentRequest request) {
        courseAppService.findCourse(courseId);
        validate(request);

        Rubric rubric = new Rubric(
                UUID.randomUUID(),
                request.rubric().title(),
                request.rubric().criteria().stream()
                        .map(criterion -> new RubricCriterion(
                                UUID.randomUUID(),
                                criterion.name(),
                                criterion.description(),
                                criterion.maxPoints()
                        ))
                        .toList()
        );
        Assignment assignment = assignmentRepository.save(new AssignmentBuilder()
                .title(request.title())
                .description(request.description())
                .dueDate(request.dueDate())
                .acceptedSubmissionTypes(request.acceptedSubmissionTypes())
                .rubric(rubric)
                .maxPoints(request.maxPoints())
                .gradingStrategyType(request.gradingStrategyType())
                .build());
        courseAssignmentRepository.link(courseId, assignment.getId());
        return AssignmentResponse.from(assignment, courseId);
    }

    public AssignmentResponse assignmentDetail(UUID assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "assignment was not found"));
        UUID courseId = courseAssignmentRepository.findCourseIdByAssignmentId(assignmentId)
                .orElse(null);
        return AssignmentResponse.from(assignment, courseId);
    }

    private void validate(CreateAssignmentRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "assignment request is required");
        }
        requireText(request.title(), "assignment title is required");
        requireText(request.description(), "assignment description is required");
        if (request.dueDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dueDate is required");
        }
        Set<SubmissionType> acceptedTypes = request.acceptedSubmissionTypes();
        if (acceptedTypes == null || acceptedTypes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "at least one submission type is required");
        }
        GradingStrategyType gradingStrategyType = request.gradingStrategyType();
        if (gradingStrategyType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "gradingStrategyType is required");
        }
        if (request.maxPoints() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "maxPoints must be positive");
        }
        validateRubric(request.rubric());
    }

    private void validateRubric(RubricRequest rubric) {
        if (rubric == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rubric is required");
        }
        requireText(rubric.title(), "rubric title is required");
        if (rubric.criteria() == null || rubric.criteria().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "at least one rubric criterion is required");
        }
        for (RubricCriterionRequest criterion : rubric.criteria()) {
            requireText(criterion.name(), "criterion name is required");
            requireText(criterion.description(), "criterion description is required");
            if (criterion.maxPoints() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "criterion maxPoints must be positive");
            }
        }
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }
}
