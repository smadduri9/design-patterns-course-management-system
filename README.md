# design-patterns-course-management-system

## Interactive Instructor App — Current Branch Setup

This section is a temporary setup note for the `interactive-instructor-ui` branch. It is not the final project README. This branch contains the React + Spring Boot interactive instructor app, with the React frontend preserving backend-driven behavior from the existing Spring Boot APIs.

### Run Locally

Start the backend from the repository root:

```bash
mvn spring-boot:run
```

The backend runs at `http://localhost:8080`.

Start the frontend from `frontend/`:

```bash
cd frontend
npm install
npm run dev
```

The frontend runs at `http://localhost:5173`.

### Existing Backend Demo Routes

- `/demo`
- `/trace`
- `/demo/phase-2`
- `/demo/phase-3`
- `/demo/phase-4`
- `/demo/phase-5`

### React App Routes

- `/`
- `/courses`
- `/courses/new`
- `/students`
- `/assignments`
- `/submissions`
- `/feedback`
- `/student-feedback`
- `/trace`

### Recommended Local Verification

```bash
mvn test
cd frontend && npm test
cd frontend && npm run build
```

Current verified status on this branch:

- Backend tests: 82 passing
- Frontend tests: 47 passing
- Frontend build: passing

### Development Rules For This Branch

- The frontend must not add fake business data.
- The frontend must not synthesize trace events.
- Backend `PatternTraceService` remains the source of trace data.
- Design-pattern logic stays in the Java backend.
- Mock AI and the Mock Java sandbox/test runner are intentional parts of the demo.

### Suggested Demo Flow

1. Start backend.
2. Start frontend.
3. Open dashboard.
4. Create course.
5. Enroll students.
6. Create assignment/rubric.
7. Create submission.
8. Run Mock AI Analysis.
9. Review feedback.
10. Send final feedback.
11. View Student Feedback.
12. View Full Trace.

## Team Members

- Sriram Madduri
- Rakshitha Srinivasa
- Ankush Rai

## Project Overview

`design-patterns-course-management-system` is an AI-assisted course management demo for a university Design Patterns course. The features are intentionally small, while the backend architecture makes exactly 18 object-oriented design patterns visible through code, tests, demo routes, and a trace panel.

The system lets an Instructor create course content, lets a Student submit PDF/text or Java code work, runs mock analysis, produces an `AIAnalysisReport`, supports instructor feedback review, and exposes final feedback to the Student.

## Problem Statement

Course-management systems often hide architectural decisions behind frameworks and data plumbing. This project keeps the product scope narrow so the design-pattern implementation is easy to inspect and demonstrate. The goal is not production AI; the goal is clear object-oriented collaboration.

## Main Workflow

1. Instructor creates a course, module, assignment, and rubric.
2. Student submits PDF/text content or Java code.
3. The submission workflow validates the request and advances submission state.
4. The system chooses the correct analyzer for the submission type.
5. PDF/text content is summarized and mapped to the rubric through a mock AI adapter.
6. Java code is passed through a mock test runner adapter, then explained through the mock AI adapter.
7. The system creates an `AIAnalysisReport` with summary, rubric findings, test results when applicable, suggested feedback, and grade suggestion.
8. Instructor edits AI feedback, saves draft snapshots, restores a prior draft if needed, and sends final feedback.
9. Student views final feedback, grade, notification, and AI summary.
10. `/trace` shows official backend pattern trace events generated during the demo flows.

## Tech Stack

- Java 21
- Spring Boot 3
- Maven
- JUnit 5
- In-memory repositories
- Basic HTML returned by Spring controllers for the landing and trace pages

## How To Run

```bash
mvn spring-boot:run
```

The application starts on `http://localhost:8080`.

## How To Test

```bash
mvn test
```

## Demo URLs

- Primary one-link demo: `http://localhost:8080/demo`
- Landing page alias: `http://localhost:8080/`
- Backup/debug Phase 2 course creation: `http://localhost:8080/demo/phase-2`
- Backup/debug Phase 3 submission workflow: `http://localhost:8080/demo/phase-3`
- Backup/debug Phase 4 mock AI analysis and Java code submission: `http://localhost:8080/demo/phase-4`
- Backup/debug Phase 5 review and notification: `http://localhost:8080/demo/phase-5`
- Design Pattern Trace Panel: `http://localhost:8080/trace`

## Mock AI And Mock Test Runner

The AI and code execution pieces are deliberately simulated. `MockAIServiceAdapter` adapts a local `MockAIService` to the internal `AIClient` interface. `MockCodeSandboxAdapter` adapts a local `MockCodeSandbox` to the internal `SandboxRunner` interface. The Java code path returns deterministic mock test results; it does not execute untrusted code.

## UI Notes

The Figma-generated frontend was used as visual guidance for cards, spacing, dashboard layout, and the right-side trace panel concept. The running demo is rendered by Spring Boot from backend data. Pattern trace events come from `PatternTraceService`, not hardcoded frontend data.

## Scope Limitations

- Only Instructor and Student roles are modeled.
- Data is stored in memory for demo clarity.
- External AI providers are not called.
- Java code is evaluated by a deterministic mock test runner.
- The UI is deliberately minimal.
- The project focuses on object-oriented design patterns rather than production platform concerns.

## Pattern Mapping

| Category | Pattern | Main Classes | Demo |
| --- | --- | --- | --- |
| Creational | Factory Method | `SubmissionAnalyzerFactory`, `TextSubmissionAnalyzerFactory`, `CodeSubmissionAnalyzerFactory` | `/demo/phase-3`, `/demo/phase-4` |
| Creational | Abstract Factory | `CourseContentFactory`, `StandardCourseContentFactory`, `ProjectBasedCourseContentFactory` | `/demo/phase-2` |
| Creational | Builder | `AssignmentBuilder` | `/demo/phase-2` |
| Structural | Composite | `CourseComponent`, `CourseComposite`, `ModuleComposite`, `AssignmentLeaf` | `/demo/phase-2` |
| Structural | Adapter | `MockAIServiceAdapter`, `MockCodeSandboxAdapter` | `/demo/phase-4` |
| Structural | Facade | `SubmissionWorkflowFacade`, `InstructorReviewFacade` | `/demo/phase-3`, `/demo/phase-5` |
| Structural | Decorator | `FeedbackGenerator`, `RubricMappedFeedbackDecorator`, `ToneAdjustedFeedbackDecorator`, `TraceableFeedbackDecorator` | `/demo/phase-4` |
| Structural | Proxy | `AnalysisService`, `CachedAnalysisServiceProxy` | `/demo/phase-4` |
| Structural | Bridge | `NotificationMessage`, `NotificationSender`, `InAppNotificationSender`, `EmailNotificationSenderMock` | `/demo/phase-5` |
| Behavioral | State | `SubmissionState`, `DraftState`, `SubmittedState`, `AnalyzingState`, `AwaitingReviewState`, `FinalizedState` | `/demo/phase-3`, `/demo/phase-5` |
| Behavioral | Strategy | `GradingStrategy`, `RubricWeightedGradingStrategy`, `PassFailGradingStrategy`, `CodeTestGradingStrategy` | `/demo/phase-4` |
| Behavioral | Observer | `DomainEventPublisher`, `DomainEventListener`, `NotificationListener`, `PatternTraceListener` | `/demo/phase-5` |
| Behavioral | Command | `CourseCommand`, `CreateCourseCommand`, `CreateAssignmentCommand`, `CommandInvoker` | `/demo/phase-2` |
| Behavioral | Chain of Responsibility | `SubmissionValidationHandler`, `SizeValidationHandler`, `AssignmentOpenValidationHandler`, `SubmissionTypeValidationHandler`, `FileTypeValidationHandler` | `/demo/phase-3` |
| Behavioral | Mediator | `SubmissionWorkflowMediator` | `/demo/phase-3` |
| Behavioral | Template Method | `AbstractSubmissionAnalyzer`, `TextSubmissionAnalyzer`, `CodeSubmissionAnalyzer` | `/demo/phase-4` |
| Behavioral | Memento | `FeedbackDraft`, `FeedbackDraftMemento`, `FeedbackDraftHistory` | `/demo/phase-5` |
| Behavioral | Iterator | `CourseComponentIterator`, `RubricCriteriaIterator` | `/demo/phase-2` |
