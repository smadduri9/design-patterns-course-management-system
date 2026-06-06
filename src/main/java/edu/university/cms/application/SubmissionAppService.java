package edu.university.cms.application;

import edu.university.cms.domain.Assignment;
import edu.university.cms.domain.Submission;
import edu.university.cms.domain.SubmissionStatus;
import edu.university.cms.domain.User;
import edu.university.cms.domain.UserRole;
import edu.university.cms.repository.AssignmentRepository;
import edu.university.cms.repository.SubmissionRepository;
import edu.university.cms.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class SubmissionAppService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    public SubmissionAppService(
            AssignmentRepository assignmentRepository,
            SubmissionRepository submissionRepository,
            UserRepository userRepository
    ) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
    }

    public List<SubmissionListItemResponse> listSubmissions(UUID assignmentId) {
        findAssignment(assignmentId);
        return submissionRepository.findAll().stream()
                .filter(submission -> submission.getAssignmentId().equals(assignmentId))
                .sorted(Comparator.comparing(Submission::getSubmittedAt))
                .map(SubmissionListItemResponse::from)
                .toList();
    }

    public SubmissionDetailResponse createSubmission(UUID assignmentId, CreateSubmissionRequest request) {
        Assignment assignment = findAssignment(assignmentId);
        validate(request);
        User student = userRepository.findById(request.studentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student was not found"));
        if (student.getRole() != UserRole.STUDENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "only students can submit assignments");
        }
        Submission submission = submissionRepository.save(new Submission(
                UUID.randomUUID(),
                assignment.getId(),
                student,
                request.submissionType(),
                request.content(),
                Instant.now(),
                SubmissionStatus.SUBMITTED
        ));
        return SubmissionDetailResponse.from(submission);
    }

    public SubmissionDetailResponse submissionDetail(UUID submissionId) {
        return SubmissionDetailResponse.from(findSubmission(submissionId));
    }

    Submission findSubmission(UUID submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "submission was not found"));
    }

    Assignment findAssignment(UUID assignmentId) {
        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "assignment was not found"));
    }

    private void validate(CreateSubmissionRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "submission request is required");
        }
        if (request.studentId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "studentId is required");
        }
        if (request.submissionType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "submissionType is required");
        }
        if (request.content() == null || request.content().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "submission content is required");
        }
    }
}
