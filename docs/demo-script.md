# Demo Script

This script is designed for a 5 to 8 minute presentation.

## 1. Start The App

Run:

```bash
mvn spring-boot:run
```

Open:

```text
http://localhost:8080/demo
```

Speaking note: "This is a small course-management system built to demonstrate object-oriented design patterns. The feature set is intentionally simple so the architecture is clear."

Use the one-link demo page first. It shows the whole project, scenario cards, Java code analysis, final feedback, and the backend-driven trace panel in one place. The phase URLs below are backup/debug routes for drilling into individual steps.

## 2. Show Course Creation

From the one-link page, point to the Course setup demo card. Backup route:

```text
http://localhost:8080/demo/phase-2
```

Point out:

- Abstract Factory creates a compatible module, assignment, and rubric set.
- Builder constructs an assignment step by step.
- Composite represents course to module to assignment.
- Iterator traverses the hierarchy.
- Command executes instructor actions and records command history.

Speaking note: "This step shows instructor-side course setup and the patterns that keep content creation structured."

## 3. Show Submission Workflow

From the one-link page, point to the text/PDF and Java code submission cards. Backup route:

```text
http://localhost:8080/demo/phase-3
```

Point out:

- Facade exposes one simple submit method.
- Mediator coordinates validation, state transitions, repository saves, and analyzer selection.
- Chain of Responsibility validates the submission.
- State moves the submission toward instructor review.
- Factory Method and Template Method prepare the analysis flow.

Speaking note: "The controller does not coordinate the workflow. It delegates to a facade, and the mediator handles the internal steps."

## 4. Show Mock AI Analysis And Java Code Submission

From the one-link page, use the Java code submission analysis card. Backup route:

```text
http://localhost:8080/demo/phase-4
```

Point out:

- A PDF/text submission is analyzed through the mock AI adapter.
- A Java code submission is analyzed through `CodeSubmissionAnalyzer`.
- `MockCodeSandboxAdapter` returns deterministic mock test results.
- `MockAIServiceAdapter` explains those test results.
- `AIAnalysisReport` includes summary, rubric findings, test results for code, suggested feedback, and grade suggestion.
- Proxy demonstrates cached analysis reuse.
- Decorator enriches suggested feedback.
- Strategy changes grading behavior.

Speaking note: "The Java code path is simulated. The project uses a mock test runner to demonstrate architecture without running untrusted code."

## 5. Show Instructor Review And Notification

From the one-link page, use the instructor review and student feedback cards. Backup route:

```text
http://localhost:8080/demo/phase-5
```

Point out:

- Memento saves and restores instructor feedback drafts.
- Final feedback changes the submission to finalized.
- Observer publishes and handles feedback finalization.
- Bridge sends a feedback notification through a sender implementation.
- Student feedback view includes final feedback, grade, notification, and AI summary.

Speaking note: "The review flow shows how final feedback is published without coupling the review service directly to notification creation."

## 6. Show Pattern Trace

The one-link page includes a right-side Design Pattern Trace Panel. For the full table view, open:

```text
http://localhost:8080/trace
```

Point out:

- Trace events come from backend code.
- Each row includes user action, official pattern, class, description, and workflow step.
- The trace panel is a demo aid, not a separate pattern implementation.

Speaking note: "The trace page is useful for the professor demo because it makes pattern activation visible."

Speaking note: "The visual style is inspired by the Figma prototype, but all trace events and scenario results come from backend services."

## 7. Close With The Pattern Count

State the official count:

- 3 creational patterns
- 6 structural patterns
- 9 behavioral patterns
- 18 total patterns

Speaking note: "The project uses exactly the required 18 design patterns, with no additional official pattern claims."
