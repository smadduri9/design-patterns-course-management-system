package edu.university.cms.web;

import edu.university.cms.application.FeedbackDraftAppService;
import edu.university.cms.application.FeedbackDraftResponse;
import edu.university.cms.application.FinalFeedbackResponse;
import edu.university.cms.application.FinalizeFeedbackRequest;
import edu.university.cms.application.RestoreFeedbackDraftRequest;
import edu.university.cms.application.SaveFeedbackDraftRequest;
import edu.university.cms.application.StudentFeedbackResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/app/submissions/{submissionId}")
public class AppFeedbackController {

    private final FeedbackDraftAppService feedbackDraftAppService;

    public AppFeedbackController(FeedbackDraftAppService feedbackDraftAppService) {
        this.feedbackDraftAppService = feedbackDraftAppService;
    }

    @GetMapping("/feedback-drafts")
    public FeedbackDraftResponse drafts(@PathVariable UUID submissionId) {
        return feedbackDraftAppService.drafts(submissionId);
    }

    @PostMapping("/feedback-drafts")
    public FeedbackDraftResponse saveDraft(
            @PathVariable UUID submissionId,
            @RequestBody SaveFeedbackDraftRequest request
    ) {
        return feedbackDraftAppService.saveDraft(submissionId, request);
    }

    @PostMapping("/feedback-drafts/restore")
    public FeedbackDraftResponse restoreDraft(
            @PathVariable UUID submissionId,
            @RequestBody RestoreFeedbackDraftRequest request
    ) {
        return feedbackDraftAppService.restoreDraft(submissionId, request);
    }

    @PostMapping("/final-feedback")
    public FinalFeedbackResponse finalizeFeedback(
            @PathVariable UUID submissionId,
            @RequestBody FinalizeFeedbackRequest request
    ) {
        return feedbackDraftAppService.finalizeFeedback(submissionId, request);
    }

    @GetMapping("/student-feedback")
    public StudentFeedbackResponse studentFeedback(@PathVariable UUID submissionId) {
        return feedbackDraftAppService.studentFeedback(submissionId);
    }
}
