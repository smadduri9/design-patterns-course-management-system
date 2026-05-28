# Pattern Map

This project demonstrates exactly 18 official design patterns. Backend classes are the source of truth, and `/trace` displays only backend-generated trace events.

| Category | Pattern | Classes Involved | Workflow Location | Why It Fits | Demo |
| --- | --- | --- | --- | --- | --- |
| Creational | Factory Method | `SubmissionAnalyzerFactory`, `TextSubmissionAnalyzerFactory`, `CodeSubmissionAnalyzerFactory`, `SubmissionAnalyzer` | Submission analysis chooses text or Java code analyzer. | The workflow depends on an analyzer abstraction while factories decide the concrete analyzer. | `/demo/phase-4`: `CodeSubmissionAnalyzerFactory` creates `CodeSubmissionAnalyzer`. |
| Creational | Abstract Factory | `CourseContentFactory`, `StandardCourseContentFactory`, `ProjectBasedCourseContentFactory` | Instructor starts a standard or project-based course setup. | Related module, assignment, and rubric templates are created as compatible families. | `/demo/phase-2`: project-based starter content is created. |
| Creational | Builder | `AssignmentBuilder`, `Assignment` | Instructor creates assignment details step by step. | Assignment creation has required and optional fields that need validation before construction. | `/demo/phase-2`: assignment template is built with rubric and accepted type. |
| Structural | Composite | `CourseComponent`, `CourseComposite`, `ModuleComposite`, `AssignmentLeaf` | Course detail hierarchy contains course, module, and assignment nodes. | Course content is tree-shaped and can be traversed through one interface. | `/demo/phase-2`: hierarchy returns course to module to assignment. |
| Structural | Adapter | `AIClient`, `MockAIService`, `MockAIServiceAdapter`, `SandboxRunner`, `MockCodeSandbox`, `MockCodeSandboxAdapter` | Text analysis calls mock AI; Java code analysis calls mock test runner and mock AI explanation. | Internal workflow stays independent from the mock services' API shapes. | `/demo/phase-4`: code analysis uses both adapters. |
| Structural | Facade | `SubmissionWorkflowFacade`, `InstructorReviewFacade` | Controllers submit assignments and finalize review through simple methods. | Controllers avoid coordinating validators, repositories, states, events, and analysis details. | `/demo/phase-3` and `/demo/phase-5`. |
| Structural | Decorator | `FeedbackGenerator`, `BasicFeedbackGenerator`, `RubricMappedFeedbackDecorator`, `ToneAdjustedFeedbackDecorator`, `TraceableFeedbackDecorator` | AI feedback is enriched before becoming suggested feedback. | Feedback layers are added without changing the base generator. | `/demo/phase-4`: suggested feedback includes rubric and tone additions. |
| Structural | Proxy | `AnalysisService`, `DefaultAnalysisService`, `CachedAnalysisServiceProxy` | Repeated analysis of the same submission checks cache before delegating. | Expensive analysis is controlled behind the same service interface. | `/demo/phase-4`: cached code report is reused. |
| Structural | Bridge | `NotificationMessage`, `FeedbackPublishedNotification`, `SubmissionReceivedNotification`, `NotificationSender`, `InAppNotificationSender`, `EmailNotificationSenderMock` | Final feedback notification can use in-app or mock email sender. | Message abstraction varies independently from delivery channel. | `/demo/phase-5`: final feedback notification is sent through in-app sender. |
| Behavioral | State | `SubmissionState`, `DraftState`, `SubmittedState`, `AnalyzingState`, `AwaitingReviewState`, `FinalizedState`, `Submission` | Submission moves through lifecycle states. | Valid operations depend on the current submission state. | `/demo/phase-3`: draft to submitted to analyzing to awaiting review; `/demo/phase-5`: finalized. |
| Behavioral | Strategy | `GradingStrategy`, `RubricWeightedGradingStrategy`, `PassFailGradingStrategy`, `CodeTestGradingStrategy` | Grade suggestion is calculated based on assignment policy. | Different grading algorithms share the same contract. | `/demo/phase-4`: text uses rubric grading; Java code uses test-result grading. |
| Behavioral | Observer | `DomainEventPublisher`, `DomainEventListener`, `NotificationListener`, `PatternTraceListener`, `FeedbackFinalizedEvent` | Feedback finalization publishes an event. | Notifications and trace logging react without the review facade calling them directly. | `/demo/phase-5`: event listeners react to `FeedbackFinalizedEvent`. |
| Behavioral | Command | `CourseCommand`, `CreateCourseCommand`, `CreateAssignmentCommand`, `CommandInvoker`, `CommandHistory` | Instructor course and assignment creation are executable command objects. | Instructor actions can be executed and recorded uniformly. | `/demo/phase-2`: command history records course and assignment creation. |
| Behavioral | Chain of Responsibility | `SubmissionValidationHandler`, `SizeValidationHandler`, `AssignmentOpenValidationHandler`, `SubmissionTypeValidationHandler`, `FileTypeValidationHandler` | Submission validation runs before acceptance. | Independent validation rules are ordered and extensible. | `/demo/phase-3`: validation handlers trace in sequence. |
| Behavioral | Mediator | `SubmissionWorkflowMediator` | Submission workflow coordinates lookup, validation, state changes, persistence, and analyzer selection. | Workflow participants do not coordinate each other directly. | `/demo/phase-3`: mediator trace surrounds the submission workflow. |
| Behavioral | Template Method | `AbstractSubmissionAnalyzer`, `TextSubmissionAnalyzer`, `CodeSubmissionAnalyzer` | Analysis follows prepare input, specialized analysis, rubric mapping, report generation. | Text and Java code analysis share the same algorithm skeleton but customize key steps. | `/demo/phase-4`: code analyzer includes mock test results. |
| Behavioral | Memento | `FeedbackDraft`, `FeedbackDraftMemento`, `FeedbackDraftHistory` | Instructor edits feedback drafts during review. | Draft state can be saved and restored without exposing internals. | `/demo/phase-5`: first feedback edit is restored before finalization. |
| Behavioral | Iterator | `CourseComponentIterator`, `RubricCriteriaIterator` | Course hierarchy and rubric criteria are traversed for display/demo. | Traversal does not expose collection internals. | `/demo/phase-2`: course tree is traversed in order. |

## Java Code Submission Analysis Trace

The Java code path is visible in `/demo/phase-4`:

1. `SubmissionType.JAVA_CODE` selects `CodeSubmissionAnalyzerFactory`.
2. The factory creates `CodeSubmissionAnalyzer`.
3. `AbstractSubmissionAnalyzer.analyze()` runs the template method skeleton.
4. `CodeSubmissionAnalyzer` calls `MockCodeSandboxAdapter`.
5. The adapter returns deterministic `TestResult` objects from `MockCodeSandbox`.
6. `CodeSubmissionAnalyzer` passes those test results to `MockAIServiceAdapter`.
7. The generated `AIAnalysisReport` contains summary, rubric findings, test results, suggested feedback, and grade suggestion.

The test runner is a mock component. It produces deterministic results and does not execute untrusted code.
