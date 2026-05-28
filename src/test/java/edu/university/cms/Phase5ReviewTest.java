package edu.university.cms;

import edu.university.cms.application.InstructorReviewFacade;
import edu.university.cms.application.InstructorReviewResult;
import edu.university.cms.application.PatternTraceService;
import edu.university.cms.application.StudentFeedbackService;
import edu.university.cms.application.StudentFeedbackView;
import edu.university.cms.domain.AIAnalysisReport;
import edu.university.cms.domain.CriterionScore;
import edu.university.cms.domain.Grade;
import edu.university.cms.domain.Notification;
import edu.university.cms.domain.OfficialPattern;
import edu.university.cms.domain.Submission;
import edu.university.cms.domain.SubmissionStatus;
import edu.university.cms.domain.SubmissionType;
import edu.university.cms.domain.User;
import edu.university.cms.domain.UserRole;
import edu.university.cms.patterns.behavioral.memento.FeedbackDraft;
import edu.university.cms.patterns.behavioral.memento.FeedbackDraftHistory;
import edu.university.cms.patterns.behavioral.observer.DomainEventListener;
import edu.university.cms.patterns.behavioral.observer.DomainEventPublisher;
import edu.university.cms.patterns.behavioral.observer.FeedbackFinalizedEvent;
import edu.university.cms.patterns.behavioral.observer.NotificationListener;
import edu.university.cms.patterns.behavioral.observer.PatternTraceListener;
import edu.university.cms.patterns.structural.bridge.EmailNotificationSenderMock;
import edu.university.cms.patterns.structural.bridge.FeedbackPublishedNotification;
import edu.university.cms.patterns.structural.bridge.InAppNotificationSender;
import edu.university.cms.repository.NotificationRepository;
import edu.university.cms.repository.SubmissionRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Phase5ReviewTest {

    @Test
    void feedbackDraftHistorySavesAndRestoresMementos() {
        PatternTraceService traceService = new PatternTraceService();
        FeedbackDraft draft = new FeedbackDraft("Initial feedback", traceService);
        FeedbackDraftHistory history = new FeedbackDraftHistory(traceService);

        history.save(draft);
        draft.edit("Updated feedback");
        history.save(draft);
        draft.restore(history.restore(0));

        assertThat(draft.getFeedbackText()).isEqualTo("Initial feedback");
        assertThat(history.findAll()).hasSize(2);
    }

    @Test
    void editingFeedbackCreatesSnapshots() {
        FeedbackDraft draft = new FeedbackDraft("AI feedback", new PatternTraceService());
        FeedbackDraftHistory history = new FeedbackDraftHistory(new PatternTraceService());

        draft.edit("Instructor edit one");
        history.save(draft);
        draft.edit("Instructor edit two");
        history.save(draft);

        assertThat(history.findAll())
                .extracting(snapshot -> snapshot.feedbackText())
                .containsExactly("Instructor edit one", "Instructor edit two");
    }

    @Test
    void feedbackFinalizationPublishesFeedbackFinalizedEvent() {
        PatternTraceService traceService = new PatternTraceService();
        RecordingListener listener = new RecordingListener();
        DomainEventPublisher publisher = new DomainEventPublisher(List.of(listener), traceService);
        FeedbackFinalizedEvent event = event();

        publisher.publish(event);

        assertThat(listener.lastEvent).isSameAs(event);
        assertThat(traceService.findAll()).extracting(trace -> trace.pattern()).contains(OfficialPattern.OBSERVER);
    }

    @Test
    void notificationListenerReactsToFeedbackFinalizedEvent() {
        PatternTraceService traceService = new PatternTraceService();
        NotificationRepository notificationRepository = new NotificationRepository();
        NotificationListener listener = new NotificationListener(notificationRepository, traceService);

        listener.onEvent(event());

        assertThat(notificationRepository.findAll()).hasSize(1);
        assertThat(notificationRepository.findAll().getFirst().getMessage()).contains("Final feedback is available");
    }

    @Test
    void bridgeSendsSameNotificationThroughInAppAndMockEmail() {
        PatternTraceService traceService = new PatternTraceService();
        User student = student();
        FeedbackPublishedNotification message = new FeedbackPublishedNotification("Great work.", new Grade(95, 100, "A"));

        Notification inApp = new InAppNotificationSender(traceService).send(student, message);
        Notification email = new EmailNotificationSenderMock(traceService).send(student, message);

        assertThat(inApp.getMessage()).contains("Final feedback is available");
        assertThat(email.getMessage()).contains("[Mock Email]").contains("Final feedback is available");
        assertThat(traceService.findAll()).extracting(trace -> trace.pattern()).containsOnly(OfficialPattern.BRIDGE);
    }

    @Test
    void finalizingFeedbackMovesSubmissionToFinalizedState() {
        TestReviewWorkflow workflow = reviewWorkflow();

        InstructorReviewResult result = workflow.facade().reviewAndFinalize(
                workflow.submission().getId(),
                "Saved feedback.",
                "Changed feedback.",
                true
        );

        assertThat(result.submission().getStatus()).isEqualTo(SubmissionStatus.FINALIZED);
        assertThat(result.submission().getGrade()).isPresent();
        assertThat(result.submission().getFinalFeedback()).contains("Saved feedback.");
    }

    @Test
    void studentCanAccessFinalFeedbackAndNotification() {
        TestReviewWorkflow workflow = reviewWorkflow();
        workflow.facade().reviewAndFinalize(workflow.submission().getId(), "Final student feedback.", "Temporary edit.", true);
        StudentFeedbackService studentFeedbackService = new StudentFeedbackService(
                workflow.submissionRepository(),
                workflow.notificationRepository()
        );

        StudentFeedbackView view = studentFeedbackService.viewFeedback(workflow.submission().getId());

        assertThat(view.finalFeedback()).isEqualTo("Final student feedback.");
        assertThat(view.grade().points()).isEqualTo(90);
        assertThat(view.notification().getMessage()).contains("Final feedback is available");
        assertThat(view.aiSummary()).isEqualTo("AI summary");
    }

    @Test
    void patternTraceServiceRecordsOnlyOfficialPhase5Patterns() {
        PatternTraceService traceService = new PatternTraceService();

        traceService.recordPhase5(OfficialPattern.MEMENTO, "FeedbackDraft", "Action", "Description", "Workflow");

        assertThatThrownBy(() -> traceService.recordPhase5(
                OfficialPattern.ADAPTER,
                "Adapter",
                "Action",
                "Description",
                "Workflow"
        )).isInstanceOf(IllegalArgumentException.class);
        assertThat(traceService.findAll()).hasSize(1);
    }

    private static TestReviewWorkflow reviewWorkflow() {
        PatternTraceService traceService = new PatternTraceService();
        SubmissionRepository submissionRepository = new SubmissionRepository();
        NotificationRepository notificationRepository = new NotificationRepository();
        NotificationListener notificationListener = new NotificationListener(notificationRepository, traceService);
        PatternTraceListener patternTraceListener = new PatternTraceListener(traceService);
        DomainEventPublisher publisher = new DomainEventPublisher(
                List.of(notificationListener, patternTraceListener),
                traceService
        );
        InstructorReviewFacade facade = new InstructorReviewFacade(
                submissionRepository,
                publisher,
                notificationListener,
                traceService
        );
        Submission submission = submission();
        submissionRepository.save(submission);
        return new TestReviewWorkflow(facade, submission, submissionRepository, notificationRepository);
    }

    private static FeedbackFinalizedEvent event() {
        return new FeedbackFinalizedEvent(UUID.randomUUID(), student(), "Final feedback.", new Grade(90, 100, "A"), Instant.now());
    }

    private static Submission submission() {
        Submission submission = new Submission(
                UUID.randomUUID(),
                UUID.randomUUID(),
                student(),
                SubmissionType.PDF_TEXT,
                "Submission content",
                Instant.now(),
                SubmissionStatus.AWAITING_REVIEW
        );
        submission.setReport(new AIAnalysisReport(
                UUID.randomUUID(),
                "AI summary",
                List.of(new CriterionScore(UUID.randomUUID(), 90, "Strong work.")),
                List.of(),
                "AI suggested feedback.",
                new Grade(90, 100, "Suggested grade")
        ));
        return submission;
    }

    private static User student() {
        return new User(UUID.randomUUID(), "Demo Student", UserRole.STUDENT);
    }

    private static class RecordingListener implements DomainEventListener<FeedbackFinalizedEvent> {
        private FeedbackFinalizedEvent lastEvent;

        @Override
        public boolean supports(edu.university.cms.patterns.behavioral.observer.DomainEvent event) {
            return event instanceof FeedbackFinalizedEvent;
        }

        @Override
        public void onEvent(FeedbackFinalizedEvent event) {
            lastEvent = event;
        }
    }

    private record TestReviewWorkflow(
            InstructorReviewFacade facade,
            Submission submission,
            SubmissionRepository submissionRepository,
            NotificationRepository notificationRepository
    ) {
    }
}
