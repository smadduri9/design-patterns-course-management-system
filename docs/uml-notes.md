# UML Notes

These notes describe diagrams to draw for the final presentation.

## Course Composite Class Diagram

```text
CourseComponent
  + getId()
  + getTitle()
  + getComponentType()
  + getChildren()

CourseComposite implements CourseComponent
  - Course course
  - List<CourseComponent> children
  + addModule(ModuleComposite)

ModuleComposite implements CourseComponent
  - CourseModule module
  - List<CourseComponent> children
  + addAssignment(AssignmentLeaf)

AssignmentLeaf implements CourseComponent
  - Assignment assignment
  + getChildren() returns empty list
```

Relationship: `CourseComposite` contains `ModuleComposite`; `ModuleComposite` contains `AssignmentLeaf`; all are traversed through `CourseComponent`.

## Submission State Diagram

```text
DraftState
  submit()
    -> SubmittedState

SubmittedState
  startAnalysis()
    -> AnalyzingState

AnalyzingState
  markAwaitingReview()
    -> AwaitingReviewState

AwaitingReviewState
  finalizeSubmission()
    -> FinalizedState
```

`Submission` delegates transitions to the current `SubmissionState`.

## AI Analysis Adapter, Decorator, And Proxy Diagram

```text
CachedAnalysisServiceProxy implements AnalysisService
  -> DefaultAnalysisService implements AnalysisService
      -> SubmissionAnalyzerFactory
          -> TextSubmissionAnalyzer
          -> CodeSubmissionAnalyzer

TextSubmissionAnalyzer
  -> AIClient
      -> MockAIServiceAdapter
          -> MockAIService

CodeSubmissionAnalyzer
  -> SandboxRunner
      -> MockCodeSandboxAdapter
          -> MockCodeSandbox
  -> AIClient
      -> MockAIServiceAdapter
          -> MockAIService

FeedbackGenerator
  -> BasicFeedbackGenerator
      wrapped by RubricMappedFeedbackDecorator
      wrapped by ToneAdjustedFeedbackDecorator
      wrapped by TraceableFeedbackDecorator
```

The proxy controls repeated analysis, adapters protect internal interfaces from mock service APIs, and decorators layer feedback enrichment.

## Observer And Bridge Notification Diagram

```text
InstructorReviewFacade
  -> DomainEventPublisher
      -> NotificationListener
          -> NotificationMessage
              -> FeedbackPublishedNotification
          -> NotificationSender
              -> InAppNotificationSender
              -> EmailNotificationSenderMock
      -> PatternTraceListener
```

Observer decouples feedback finalization from listeners. Bridge decouples notification message type from delivery channel.

## Memento Feedback Draft Diagram

```text
FeedbackDraft
  - feedbackText
  + edit(text)
  + save(): FeedbackDraftMemento
  + restore(memento)

FeedbackDraftMemento
  - feedbackText
  - savedAt

FeedbackDraftHistory
  - List<FeedbackDraftMemento>
  + save(draft)
  + restore(index)
```

The instructor can save feedback draft snapshots and restore an earlier draft without exposing draft internals.

## End-To-End Sequence Diagram

```text
Instructor -> CourseContentFactory: create starter content
CourseContentFactory -> AssignmentBuilder: build assignment
CommandInvoker -> CreateCourseCommand: save course
CommandInvoker -> CreateAssignmentCommand: save assignment

Student -> SubmissionWorkflowFacade: submitAssignment()
SubmissionWorkflowFacade -> SubmissionWorkflowMediator: submit()
SubmissionWorkflowMediator -> SubmissionValidationHandler chain: validate()
Submission -> SubmissionState: submit/startAnalysis/markAwaitingReview
SubmissionWorkflowMediator -> SubmissionAnalyzerFactory: createAnalyzer()
AbstractSubmissionAnalyzer -> Mock adapters: analyze content
AbstractSubmissionAnalyzer -> GradingStrategy: calculate grade
AbstractSubmissionAnalyzer -> FeedbackGenerator decorators: enrich feedback
SubmissionWorkflowMediator -> SubmissionRepository: save report

Instructor -> InstructorReviewFacade: reviewAndFinalize()
InstructorReviewFacade -> FeedbackDraftHistory: save/restore mementos
Submission -> SubmissionState: finalizeSubmission()
InstructorReviewFacade -> DomainEventPublisher: publish FeedbackFinalizedEvent
DomainEventPublisher -> NotificationListener: onEvent()
NotificationListener -> NotificationSender: send message
Student -> StudentFeedbackService: view final feedback
```
