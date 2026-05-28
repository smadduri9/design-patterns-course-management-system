---
name: Course Patterns Plan
overview: Greenfield implementation plan for an AI-assisted course management system focused on exactly 18 explicit design pattern demonstrations. The plan keeps features intentionally small while making the object-oriented architecture, pattern traceability, and professor demo story strong.
todos:
  - id: skeleton
    content: Create Java Spring Boot Maven skeleton with simple UI and in-memory repositories.
    status: completed
  - id: domain
    content: Implement core domain entities and seeded users/courses/submissions.
    status: completed
  - id: course-patterns
    content: "Implement course creation patterns: Abstract Factory, Builder, Composite, Iterator, Command."
    status: pending
  - id: submission-patterns
    content: "Implement submission workflow patterns: State, Chain of Responsibility, Mediator, Facade, Factory Method, Template Method."
    status: pending
  - id: analysis-patterns
    content: "Implement mock AI/code analysis patterns: Adapter, Proxy, Decorator, Strategy."
    status: pending
  - id: review-trace
    content: "Implement review, notifications, and trace patterns: Memento, Observer, Bridge, Pattern Trace Panel with only the official 18 patterns."
    status: pending
  - id: tests-docs
    content: Add focused tests, README, pattern map, and demo checklist for all 18 patterns.
    status: completed
isProject: false
---

# design-patterns-course-management-system Plan

Project name: `design-patterns-course-management-system`.

Important contract: the backend/domain design-pattern implementation is the source of truth. The UI may reuse visual ideas from the Figma-generated React frontend in [figma/Untitled](figma/Untitled), but the frontend must remain thin and must not introduce, rename, simulate, or count any additional patterns. The official pattern count is exactly 18.

## 1. Recommended Tech Stack

Use a Java-first stack because the course goal is object-oriented architecture and design pattern clarity.

- Language: Java 21
- Build tool: Maven
- App framework: Spring Boot 3, used lightly for HTTP routing and app startup only
- UI: Thymeleaf server-rendered pages with simple HTML/CSS, or a very small React/Vite UI adapted from [figma/Untitled](figma/Untitled) if the team prefers that look
- Storage: In-memory repositories seeded with mock data
- Testing: JUnit 5
- Optional frontend enhancement: minimal JavaScript for the Pattern Trace Panel

Key files to create:

- [pom.xml](pom.xml)
- [src/main/java/edu/university/cms/CourseManagementApplication.java](src/main/java/edu/university/cms/CourseManagementApplication.java)
- [src/main/resources/templates](src/main/resources/templates)
- [src/test/java/edu/university/cms](src/test/java/edu/university/cms)

Why this stack fits:

- Java makes interfaces, abstract classes, inheritance, composition, and polymorphism visible.
- Spring Boot avoids boilerplate web setup but should not be used as the pattern implementation itself.
- Thymeleaf keeps the web UI simple and avoids frontend complexity. If the Figma React UI is reused, keep React components presentational and fetch/display backend state only.
- In-memory repositories preserve the project focus on patterns instead of persistence.

Frontend reuse rule:

- Reuse only the simple screen concepts from the Figma export: instructor dashboard, course builder, student submission, submission review, and pattern trace.
- Remove generated UI concepts that expand scope, such as extra management surfaces, conversational-assistant interactions, assessment-generation flows, access-control flows, and unrelated dashboard widgets.
- Remove any Figma sample trace entry that is not in the official 18.
- Do not treat React components, hooks, routes, shadcn components, or frontend state management as design-pattern demonstrations.
- Pattern names shown in the UI must come from the backend `PatternTraceService` allowlist, not hardcoded frontend demo data.

## 2. Package And Folder Structure

Recommended structure:

- [src/main/java/edu/university/cms/domain](src/main/java/edu/university/cms/domain)
  - Core entities: `User`, `Course`, `Module`, `Assignment`, `Rubric`, `RubricCriterion`, `Submission`, `AIAnalysisReport`, `Grade`, `Notification`
  - Enums/value objects: `SubmissionType`, `SubmissionStatus`, `UserRole`, `CriterionScore`, `TestResult`

- [src/main/java/edu/university/cms/patterns/creational](src/main/java/edu/university/cms/patterns/creational)
  - `SubmissionAnalyzerFactory`, `CourseContentFactory`, `AssignmentBuilder`

- [src/main/java/edu/university/cms/patterns/structural](src/main/java/edu/university/cms/patterns/structural)
  - Composite course content, adapters, facade, decorators, proxy, bridge abstractions

- [src/main/java/edu/university/cms/patterns/behavioral](src/main/java/edu/university/cms/patterns/behavioral)
  - State, strategy, observer, command, chain, mediator, template, memento, iterator classes

- [src/main/java/edu/university/cms/application](src/main/java/edu/university/cms/application)
  - Use-case services: `CourseManagementFacade`, `SubmissionWorkflowService`, `InstructorReviewService`, `PatternTraceService`

- [src/main/java/edu/university/cms/repository](src/main/java/edu/university/cms/repository)
  - In-memory repositories: `CourseRepository`, `AssignmentRepository`, `SubmissionRepository`, `NotificationRepository`

- [src/main/java/edu/university/cms/web](src/main/java/edu/university/cms/web)
  - Controllers and simple DTOs

- [src/main/resources/templates](src/main/resources/templates)
  - `dashboard.html`, `course-detail.html`, `assignment-detail.html`, `submit-assignment.html`, `review-submission.html`, `student-feedback.html`, `pattern-trace.html`

- Optional simple React frontend, only if adapting the Figma export:
  - [frontend/src/App.tsx](frontend/src/App.tsx)
  - [frontend/src/components/InstructorDashboard.tsx](frontend/src/components/InstructorDashboard.tsx)
  - [frontend/src/components/CourseBuilder.tsx](frontend/src/components/CourseBuilder.tsx)
  - [frontend/src/components/StudentSubmission.tsx](frontend/src/components/StudentSubmission.tsx)
  - [frontend/src/components/SubmissionReview.tsx](frontend/src/components/SubmissionReview.tsx)
  - [frontend/src/components/PatternTracePanel.tsx](frontend/src/components/PatternTracePanel.tsx)
  - Keep this frontend as a view layer over backend APIs; do not implement design patterns here.

## 3. Exact Mapping Of All 18 Patterns

### Creational Patterns

1. Factory Method

- Where it appears: submission analysis creation.
- Why justified: PDF/text submissions and code submissions require different analyzers, but the workflow should depend on a common abstraction.
- Classes: `SubmissionAnalyzerFactory`, `TextSubmissionAnalyzerFactory`, `CodeSubmissionAnalyzerFactory`, `SubmissionAnalyzer`, `TextSubmissionAnalyzer`, `CodeSubmissionAnalyzer`.
- Workflow demonstration: after a student submits work, the system detects `SubmissionType` and asks the matching factory to create the analyzer.

2. Abstract Factory

- Where it appears: creating families of course content objects.
- Why justified: an instructor workflow creates related objects such as course modules, assignments, and rubrics that should be consistent for a course type.
- Classes: `CourseContentFactory`, `StandardCourseContentFactory`, `ProjectBasedCourseContentFactory`, `ModuleTemplate`, `AssignmentTemplate`, `RubricTemplate`.
- Workflow demonstration: instructor creates a course and chooses a standard or project-based setup; the factory creates compatible module, assignment, and rubric templates.

3. Builder

- Where it appears: assignment creation.
- Why justified: assignments have optional and required parts such as title, description, due date, accepted submission types, rubric, and max points.
- Classes: `AssignmentBuilder`, `Assignment`.
- Workflow demonstration: instructor creates an assignment step-by-step, then the builder validates and produces the final `Assignment` object.

### Structural Patterns

4. Composite

- Where it appears: course content tree.
- Why justified: a course contains modules, modules contain assignments, and the UI should render the hierarchy uniformly.
- Classes: `CourseComponent`, `CourseComposite`, `ModuleComposite`, `AssignmentLeaf`.
- Workflow demonstration: course detail page iterates over the course tree and displays modules and assignments through the same interface.

5. Adapter

- Where it appears: external/mock AI and sandbox integrations.
- Why justified: internal services should not depend directly on mock AI or mock test runner APIs.
- Classes: `AIClient`, `MockAIService`, `MockAIServiceAdapter`, `SandboxRunner`, `MockCodeSandbox`, `MockCodeSandboxAdapter`.
- Workflow demonstration: text analysis calls `AIClient`; code analysis calls `SandboxRunner`, even though both are backed by mock implementations.

6. Facade

- Where it appears: high-level app operations.
- Why justified: controllers should not coordinate factories, analyzers, reports, notifications, command history, and trace logging directly.
- Classes: `CourseManagementFacade`, `SubmissionWorkflowService`, `InstructorReviewService`.
- Workflow demonstration: web controllers call simple facade methods such as `submitAssignment`, `reviewFeedback`, and `createCourse`.

7. Decorator

- Where it appears: enriching generated feedback.
- Why justified: feedback can be layered with rubric mapping, tone adjustment, and summary without changing the base generator.
- Classes: `FeedbackGenerator`, `BasicFeedbackGenerator`, `RubricMappedFeedbackDecorator`, `ToneAdjustedFeedbackDecorator`, `TraceableFeedbackDecorator`.
- Workflow demonstration: AI report generation decorates base feedback before the instructor reviews it.

8. Proxy

- Where it appears: AI analysis access and expensive analysis calls.
- Why justified: AI analysis should be controlled, cached, and traceable even though the implementation is mocked.
- Classes: `AnalysisService`, `MockAnalysisService`, `CachedAnalysisServiceProxy`.
- Workflow demonstration: repeated viewing of a submission report uses the proxy cache instead of regenerating analysis.

9. Bridge

- Where it appears: notification delivery.
- Why justified: notification type should vary independently from delivery channel.
- Classes: `NotificationMessage`, `FeedbackPublishedNotification`, `SubmissionReceivedNotification`, `NotificationSender`, `EmailNotificationSender`, `InAppNotificationSender`.
- Workflow demonstration: when final feedback is published, the same notification abstraction can be sent through in-app or mock email channels.

### Behavioral Patterns

10. State

- Where it appears: submission lifecycle.
- Why justified: allowed actions change based on status: draft, submitted, analyzing, awaiting instructor review, finalized.
- Classes: `SubmissionState`, `DraftState`, `SubmittedState`, `AnalyzingState`, `AwaitingReviewState`, `FinalizedState`, `Submission`.
- Workflow demonstration: submit, analyze, review, and finalize transitions are delegated to state objects.

11. Strategy

- Where it appears: grading and analysis scoring.
- Why justified: different assignments may use rubric-weighted grading, pass/fail grading, or code-test-based grading.
- Classes: `GradingStrategy`, `RubricWeightedGradingStrategy`, `PassFailGradingStrategy`, `CodeTestGradingStrategy`.
- Workflow demonstration: instructor selects grading mode for an assignment; final grade calculation uses the selected strategy.

12. Observer

- Where it appears: notifications and trace events.
- Why justified: multiple components should react when submissions are analyzed or feedback is finalized without tight coupling.
- Classes: `DomainEventPublisher`, `DomainEventListener`, `NotificationListener`, `PatternTraceListener`, `SubmissionAnalyzedEvent`, `FeedbackFinalizedEvent`.
- Workflow demonstration: finalizing feedback publishes an event; notification creation and pattern trace logging happen as observers.

13. Command

- Where it appears: instructor actions.
- Why justified: create/review/finalize actions can be represented as objects, enabling history and undo for selected operations.
- Classes: `CourseCommand`, `CreateCourseCommand`, `CreateAssignmentCommand`, `ReviewFeedbackCommand`, `CommandInvoker`, `CommandHistory`.
- Workflow demonstration: instructor creates an assignment and reviews feedback through commands; command history appears in the trace panel.

14. Chain of Responsibility

- Where it appears: submission validation pipeline.
- Why justified: validation has multiple independent checks that should be ordered and extensible.
- Classes: `SubmissionValidationHandler`, `FileTypeValidationHandler`, `SizeValidationHandler`, `AssignmentOpenValidationHandler`, `SubmissionTypeValidationHandler`.
- Workflow demonstration: before a submission is accepted, it passes through the validation chain.

15. Mediator

- Where it appears: coordinating submission analysis.
- Why justified: repositories, analyzers, graders, report builders, and notifications should not call each other directly.
- Classes: `SubmissionWorkflowMediator`, `SubmissionRepository`, `AnalysisService`, `GradingStrategy`, `DomainEventPublisher`.
- Workflow demonstration: after validation, the mediator coordinates analysis, report creation, grading, state transition, and event publishing.

16. Template Method

- Where it appears: common analysis algorithm.
- Why justified: text and code analysis share the same overall sequence but differ in extraction and scoring details.
- Classes: `AbstractSubmissionAnalyzer`, `TextSubmissionAnalyzer`, `CodeSubmissionAnalyzer`.
- Workflow demonstration: both submission types run `analyze()` with the same skeleton: prepare input, run specialized analysis, map rubric, generate report.

17. Memento

- Where it appears: instructor feedback draft history.
- Why justified: instructors may edit AI feedback and restore a previous feedback draft before publishing.
- Classes: `FeedbackDraft`, `FeedbackDraftMemento`, `FeedbackDraftHistory`.
- Workflow demonstration: instructor edits AI feedback, saves snapshots, and restores an earlier draft in the review screen.

18. Iterator

- Where it appears: traversing course modules, assignments, rubric criteria, and trace entries.
- Why justified: UI and reports should traverse collections without exposing internal storage.
- Classes: `CourseComponentIterator`, `RubricCriteriaIterator`, `PatternTraceIterator`.
- Workflow demonstration: course detail and pattern trace panel render ordered content using iterators.

## 4. Core Class Diagram In Text Form

```text
User
- id
- name
- role

Course
- id
- title
- instructor: User
- rootContent: CourseComposite

CourseComponent interface
- getTitle()
- getChildren()
- accept/display operation

CourseComposite implements CourseComponent
- modules: List<CourseComponent>

ModuleComposite implements CourseComponent
- assignments: List<CourseComponent>

AssignmentLeaf implements CourseComponent
- assignment: Assignment

Assignment
- id
- title
- description
- acceptedTypes: List<SubmissionType>
- rubric: Rubric
- gradingStrategy: GradingStrategy

Rubric
- id
- title
- criteria: List<RubricCriterion>

RubricCriterion
- name
- description
- maxPoints

Submission
- id
- assignmentId
- student: User
- content
- type: SubmissionType
- state: SubmissionState
- report: AIAnalysisReport
- grade: Grade

AIAnalysisReport
- id
- summary
- rubricFindings
- testResults
- suggestedFeedback

Grade
- points
- maxPoints
- comments

Notification
- id
- recipient: User
- message: NotificationMessage
- sender: NotificationSender

CourseManagementFacade
- createCourse()
- createAssignment()
- submitAssignment()
- reviewFeedback()
- publishFeedback()

SubmissionWorkflowMediator
- validateSubmission()
- analyzeSubmission()
- gradeSubmission()
- publishEvents()
```

## 5. Main Workflow Sequence In Text Form

```text
1. Instructor opens Create Course screen.
2. Controller calls CourseManagementFacade.createCourse().
3. CommandInvoker executes CreateCourseCommand.
4. CourseContentFactory creates initial module, assignment, and rubric templates.
5. Instructor creates or edits module and assignment.
6. AssignmentBuilder builds Assignment with rubric and accepted submission types.
7. Student opens assignment and uploads PDF/text content or Java code content.
8. CourseManagementFacade.submitAssignment() receives the request.
9. Submission validation runs through Chain of Responsibility.
10. Submission enters SubmittedState, then AnalyzingState.
11. SubmissionWorkflowMediator selects the correct SubmissionAnalyzerFactory.
12. Factory Method creates TextSubmissionAnalyzer or CodeSubmissionAnalyzer.
13. AbstractSubmissionAnalyzer.analyze() runs Template Method steps.
14. Text flow calls AIClient through MockAIServiceAdapter.
15. Java code flow calls SandboxRunner through MockCodeSandboxAdapter, then AIClient explains mock test results.
16. AnalysisService call passes through CachedAnalysisServiceProxy.
17. FeedbackGenerator decorators enrich the feedback.
18. GradingStrategy calculates a suggested grade.
19. AIAnalysisReport is attached to Submission.
20. DomainEventPublisher notifies observers that analysis is complete.
21. Instructor opens Review Submission screen to edit AI feedback.
22. Instructor edits feedback draft; Memento stores draft snapshots.
23. Instructor sends final feedback through ReviewFeedbackCommand.
24. Submission enters FinalizedState.
25. FeedbackFinalizedEvent triggers NotificationListener and PatternTraceListener.
26. Bridge sends notification through selected NotificationSender.
27. Student opens final feedback.
28. Pattern Trace Panel displays all activated patterns using PatternTraceIterator.
```

## 6. Suggested UI Screens

- Instructor Dashboard: project header for `design-patterns-course-management-system`, course list, create course button, and submissions needing review.
- Course Builder: create course, module, assignment, and rubric in one simple instructor flow.
- Course Detail: course hierarchy rendered from backend Composite and Iterator results.
- Student Assignment View: assignment details and simple submission form for PDF/text or Java code.
- Submission Analysis Result: AIAnalysisReport summary, rubric mapping, and mock sandbox results for Java code submissions.
- Instructor Review: editable AI feedback, grade editor, restore previous draft button, and send final feedback button.
- Student Feedback: final grade, final feedback, AI summary, and notification status.
- Pattern Trace Panel: action timeline showing only official backend-reported pattern activations.

Figma screen adaptation notes:

- Keep the generated screen set conceptually close to `InstructorDashboard`, `CourseBuilder`, `StudentSubmission`, `SubmissionReview`, and `DesignPatternTrace`.
- Rename `DesignPatternTrace` to `PatternTracePanel` if implemented in the final app.
- Replace all hardcoded sample trace data with backend trace events.
- Replace sample users and names with the two supported roles: Instructor and Student.
- Remove any approve/reject wording from the review screen; the instructor reviews AI feedback, edits if needed, and sends final feedback.

## 7. Pattern Trace Panel Design

Purpose: make the design patterns visible during the demo.

Core model:

- `PatternTraceEvent`
  - `timestamp`
  - `userAction`
  - `patternName`
  - `category`
  - `className`
  - `description`
  - `workflowStep`

Trace capture:

- `PatternTraceService.record(patternName, className, action, description)` is called at meaningful pattern activation points.
- `PatternTraceService` validates `patternName` against an `OfficialPattern` enum containing exactly the 18 approved patterns.
- `PatternTraceListener` also records observer-driven events.
- `TraceableFeedbackDecorator` records decorator activation without mixing trace logic into every feedback generator.

UI behavior:

- Right-side panel or separate page named `Pattern Trace`.
- Filter by user action: create course, create assignment, submit work, analyze work, review feedback, publish feedback.
- Filter by pattern category: creational, structural, behavioral.
- Each trace row shows: action, pattern, class, short explanation.
- The panel must never show any pattern outside the official list.

Example trace entries:

- `Student submitted code file -> Chain of Responsibility -> FileTypeValidationHandler validated extension`
- `System analyzed code -> Factory Method -> CodeSubmissionAnalyzerFactory created CodeSubmissionAnalyzer`
- `System generated report -> Template Method -> AbstractSubmissionAnalyzer executed shared analysis flow`
- `Instructor edited feedback -> Memento -> FeedbackDraftHistory saved draft snapshot`
- `Feedback published -> Bridge -> FeedbackPublishedNotification sent through InAppNotificationSender`

## 8. Implementation Phases

Phase 1: Project skeleton and domain model

- Create Maven/Spring Boot skeleton.
- Add core entities, enums, and in-memory repositories.
- Seed simple users: one Instructor and one Student role, with no access-control system.
- Add `OfficialPattern` enum or equivalent allowlist containing exactly the 18 required patterns.

Phase 2: Course and assignment creation

- Implement Composite for course/module/assignment hierarchy.
- Implement Abstract Factory for initial course content templates.
- Implement Builder for assignment creation.
- Add instructor dashboard and course detail screens.

Phase 3: Submission workflow

- Implement Submission State lifecycle.
- Implement Chain of Responsibility for validation.
- Implement Mediator and Facade for submission orchestration.
- Add student submission screen.

Phase 4: AI analysis and grading

- Implement Factory Method for analyzer creation.
- Implement Template Method in the base analyzer.
- Implement Adapter for mock AI and mock sandbox.
- Implement Proxy for cached analysis.
- Implement Strategy for grading.
- Implement Decorator for feedback enrichment.

Phase 5: Review, notifications, and traceability

- Implement Command for instructor actions.
- Implement Memento for feedback draft history.
- Implement Observer for events.
- Implement Bridge for notifications.
- Implement Iterator for course content and trace rendering.
- Build instructor review, student feedback, and Pattern Trace Panel screens.
- If adapting the Figma UI, strip hardcoded demo traces and wire the panel to backend trace events only.

Phase 6: Tests and demo polish

- Add focused JUnit tests for each pattern.
- Add a demo seed scenario that exercises all 18 patterns.
- Add README documentation mapping each pattern to classes and workflow steps.

## 9. Things To Avoid

- Do not add an access-control system; use a simple role switcher or seeded users.
- Do not add any roles beyond Instructor and Student.
- Do not add a production database; in-memory repositories are enough.
- Do not add generated assessments, conversational-assistant features, analytics dashboards, extra management screens, approve/reject feedback flows, or external AI features.
- Do not integrate external AI APIs in the first version.
- Do not run untrusted code; the sandbox should be a mock test runner.
- Do not let Spring dependency injection count as one of the design patterns.
- Do not add more than exactly 18 named pattern implementations.
- Do not show any pattern outside the official 18 in the Pattern Trace Panel.
- Do not create multiple competing versions of the same pattern just to make the system look larger.
- Do not bury pattern examples inside controllers; keep them in clear domain/application classes.
- Do not overbuild permissions, enrollment, file storage, or course catalog features.

## 10. Professor And Demo Checklist

Use this checklist during the final demo:

- Exactly 18 official patterns are present.
- Factory Method: submit text and code submissions; show different analyzers being created.
- Abstract Factory: create a standard or project-based course setup; show related templates generated together.
- Builder: create an assignment from form fields; show `AssignmentBuilder` validation and final object creation.
- Composite: open course detail; show course, modules, and assignments rendered as one hierarchy.
- Adapter: show mock AI and mock sandbox wrapped behind internal interfaces.
- Facade: show controllers calling `CourseManagementFacade` instead of many services directly.
- Decorator: show feedback enhanced with rubric mapping and tone adjustment.
- Proxy: open the same report twice; show cached analysis reuse.
- Bridge: publish feedback and send notification through in-app or mock email sender.
- State: show submission status moving from submitted to analyzing to awaiting review to finalized.
- Strategy: switch grading strategy for an assignment and show a different grade calculation.
- Observer: publish feedback and show notification plus trace event created from the same domain event.
- Command: show instructor create/review action recorded in command history.
- Chain of Responsibility: submit invalid then valid files; show validation handlers.
- Mediator: show submission workflow coordinated by `SubmissionWorkflowMediator`.
- Template Method: show text and code analyzers sharing the same `analyze()` skeleton.
- Memento: edit instructor feedback, restore previous draft, then publish.
- Iterator: show ordered traversal of course content or Pattern Trace events.

## 11. Documentation Deliverables

Create these docs alongside the implementation:

- [README.md](README.md): setup, run instructions, feature summary, and demo script.
- [docs/pattern-map.md](docs/pattern-map.md): exact 18-pattern mapping with classes and workflows.
- [docs/demo-checklist.md](docs/demo-checklist.md): professor-facing walkthrough.

The implementation should treat these 18 patterns as a fixed contract. If a later feature appears to require another pattern, simplify the feature or document it as ordinary implementation detail only. Do not add it to the official pattern count and do not display it in the Pattern Trace Panel.