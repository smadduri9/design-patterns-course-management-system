package edu.university.cms.web;

import edu.university.cms.application.AssignmentAppService;
import edu.university.cms.application.AssignmentResponse;
import edu.university.cms.application.CreateAssignmentRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/app")
public class AppAssignmentController {

    private final AssignmentAppService assignmentAppService;

    public AppAssignmentController(AssignmentAppService assignmentAppService) {
        this.assignmentAppService = assignmentAppService;
    }

    @GetMapping("/courses/{courseId}/assignments")
    public List<AssignmentResponse> assignments(@PathVariable UUID courseId) {
        return assignmentAppService.listAssignments(courseId);
    }

    @PostMapping("/courses/{courseId}/assignments")
    public AssignmentResponse createAssignment(
            @PathVariable UUID courseId,
            @RequestBody CreateAssignmentRequest request
    ) {
        return assignmentAppService.createAssignment(courseId, request);
    }

    @GetMapping("/assignments/{assignmentId}")
    public AssignmentResponse assignmentDetail(@PathVariable UUID assignmentId) {
        return assignmentAppService.assignmentDetail(assignmentId);
    }
}
