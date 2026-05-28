package edu.university.cms.domain;

import edu.university.cms.application.PatternTraceService;
import edu.university.cms.patterns.behavioral.state.AnalyzingState;
import edu.university.cms.patterns.behavioral.state.AwaitingReviewState;
import edu.university.cms.patterns.behavioral.state.DraftState;
import edu.university.cms.patterns.behavioral.state.FinalizedState;
import edu.university.cms.patterns.behavioral.state.SubmissionState;
import edu.university.cms.patterns.behavioral.state.SubmittedState;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Submission {

    private final UUID id;
    private final UUID assignmentId;
    private final User student;
    private final SubmissionType type;
    private final String content;
    private final Instant submittedAt;
    private SubmissionStatus status;
    private SubmissionState state;
    private AIAnalysisReport report;
    private Grade grade;
    private String finalFeedback;

    public Submission(
            UUID id,
            UUID assignmentId,
            User student,
            SubmissionType type,
            String content,
            Instant submittedAt,
            SubmissionStatus status
    ) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.assignmentId = Objects.requireNonNull(assignmentId, "assignmentId is required");
        this.student = Objects.requireNonNull(student, "student is required");
        if (student.getRole() != UserRole.STUDENT) {
            throw new IllegalArgumentException("submission owner must be a student");
        }
        this.type = Objects.requireNonNull(type, "type is required");
        this.content = requireText(content, "content is required");
        this.submittedAt = Objects.requireNonNull(submittedAt, "submittedAt is required");
        this.status = Objects.requireNonNull(status, "status is required");
        this.state = stateFor(status);
    }

    public UUID getId() {
        return id;
    }

    public UUID getAssignmentId() {
        return assignmentId;
    }

    public User getStudent() {
        return student;
    }

    public SubmissionType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = Objects.requireNonNull(status, "status is required");
        this.state = stateFor(status);
    }

    public SubmissionState getState() {
        return state;
    }

    public void submit(PatternTraceService traceService) {
        state.submit(this, traceService);
    }

    public void startAnalysis(PatternTraceService traceService) {
        state.startAnalysis(this, traceService);
    }

    public void markAwaitingReview(PatternTraceService traceService) {
        state.markAwaitingReview(this, traceService);
    }

    public void finalizeSubmission(PatternTraceService traceService) {
        state.finalizeSubmission(this, traceService);
    }

    public void transitionTo(SubmissionState nextState) {
        this.state = Objects.requireNonNull(nextState, "nextState is required");
        this.status = nextState.status();
    }

    public Optional<AIAnalysisReport> getReport() {
        return Optional.ofNullable(report);
    }

    public void setReport(AIAnalysisReport report) {
        this.report = Objects.requireNonNull(report, "report is required");
    }

    public Optional<Grade> getGrade() {
        return Optional.ofNullable(grade);
    }

    public void setGrade(Grade grade) {
        this.grade = Objects.requireNonNull(grade, "grade is required");
    }

    public Optional<String> getFinalFeedback() {
        return Optional.ofNullable(finalFeedback);
    }

    public void setFinalFeedback(String finalFeedback) {
        this.finalFeedback = requireText(finalFeedback, "finalFeedback is required");
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private static SubmissionState stateFor(SubmissionStatus status) {
        return switch (status) {
            case DRAFT -> new DraftState();
            case SUBMITTED -> new SubmittedState();
            case ANALYZING -> new AnalyzingState();
            case AWAITING_REVIEW -> new AwaitingReviewState();
            case FINALIZED -> new FinalizedState();
        };
    }
}
