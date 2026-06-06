package edu.university.cms.web;

import edu.university.cms.application.AnalysisAppService;
import edu.university.cms.application.AnalysisResponse;
import edu.university.cms.application.CreateSubmissionRequest;
import edu.university.cms.application.SubmissionAppService;
import edu.university.cms.application.SubmissionDetailResponse;
import edu.university.cms.application.SubmissionListItemResponse;
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
public class AppSubmissionController {

    private final SubmissionAppService submissionAppService;
    private final AnalysisAppService analysisAppService;

    public AppSubmissionController(SubmissionAppService submissionAppService, AnalysisAppService analysisAppService) {
        this.submissionAppService = submissionAppService;
        this.analysisAppService = analysisAppService;
    }

    @GetMapping("/assignments/{assignmentId}/submissions")
    public List<SubmissionListItemResponse> submissions(@PathVariable UUID assignmentId) {
        return submissionAppService.listSubmissions(assignmentId);
    }

    @PostMapping("/assignments/{assignmentId}/submissions")
    public SubmissionDetailResponse createSubmission(
            @PathVariable UUID assignmentId,
            @RequestBody CreateSubmissionRequest request
    ) {
        return submissionAppService.createSubmission(assignmentId, request);
    }

    @GetMapping("/submissions/{submissionId}")
    public SubmissionDetailResponse submissionDetail(@PathVariable UUID submissionId) {
        return submissionAppService.submissionDetail(submissionId);
    }

    @PostMapping("/submissions/{submissionId}/analyze")
    public AnalysisResponse analyzeSubmission(@PathVariable UUID submissionId) {
        return analysisAppService.analyzeSubmission(submissionId);
    }
}
