# Course Management System

> An AI-assisted course management platform built to make **18 object-oriented design patterns** observable through real, running code.

[![Backend](https://img.shields.io/badge/backend-Spring%20Boot%203.3-6db33f)](#tech-stack)
[![Frontend](https://img.shields.io/badge/frontend-React%2019%20%2B%20Vite-4f46e5)](#tech-stack)
[![Java](https://img.shields.io/badge/Java-21-007396)](#tech-stack)
[![Tests](https://img.shields.io/badge/tests-backend%2082%20%C2%B7%20frontend%2047-2ea44f)](#testing)

The product scope is intentionally focused: an instructor creates courses, assignments, and rubrics; a student submits PDF/text or Java code; the system runs a mock AI analysis and produces an `AIAnalysisReport`; the instructor reviews and finalizes feedback; and the student sees the result. Every meaningful step is driven by a classic design pattern in the Spring Boot backend, and each pattern execution is recorded by `PatternTraceService` so it can be inspected live on the **Full Trace** page.

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Design Patterns](#design-patterns)
- [Getting Started](#getting-started)
- [Testing](#testing)
- [Application Routes](#application-routes)
- [Demo Flow](#demo-flow)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Scope & Limitations](#scope--limitations)
- [Team](#team)

---

## Overview

Course-management systems often hide their architecture behind frameworks and data plumbing. This project deliberately keeps the feature set small so the object-oriented design is easy to read, test, and demonstrate. The goal is not production AI — it is clear, well-structured collaboration between objects.

**Core capabilities**

- Instructors build course content (courses, modules, assignments, rubrics).
- Students submit PDF/text essays or Java code.
- Submissions are validated and advanced through a state machine.
- A mock AI analyzer and a mock Java test runner produce a structured `AIAnalysisReport`.
- Instructors edit feedback, snapshot drafts, restore earlier versions, and send final feedback.
- Students view their finalized feedback, grade, and notification.
- A live trace surfaces every design-pattern execution behind these flows.

## Architecture

The system is split into a backend that owns all business logic and a frontend that renders it.

- **Backend (Spring Boot)** — Holds every design-pattern implementation, the submission workflow, the mock AI/sandbox adapters, and `PatternTraceService`. It exposes REST APIs under `/api/app/**` plus scripted demo routes under `/demo/**`.
- **Frontend (React)** — An instructor-facing single-page app that consumes the backend APIs. It never fabricates business data or trace events; it only displays what the backend returns. During development it proxies `/api` to `http://localhost:8080`.

This separation is intentional: the frontend can evolve independently, while the patterns and their trace remain the single source of truth in Java.

## Design Patterns

All 18 patterns are implemented in the backend and emit trace events during the demo flows.

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

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- Node.js 20+ and npm

### 1. Start the backend

From the repository root:

```bash
mvn spring-boot:run
```

The API runs at `http://localhost:8080`. Data is held in memory, so restarting the backend resets all courses, submissions, and trace events.

### 2. Start the frontend

From `frontend/`:

```bash
cd frontend
npm install
npm run dev
```

The app runs at `http://localhost:5173` and proxies API calls to the backend.

## Testing

```bash
# Backend unit/integration tests
mvn test

# Frontend tests
cd frontend && npm test

# Frontend production build
cd frontend && npm run build
```

Verified status: **backend 82 tests passing**, **frontend 47 tests passing**, **frontend build passing**.

## Application Routes

**React app (`http://localhost:5173`)**

| Route | Purpose |
| --- | --- |
| `/` | Dashboard overview |
| `/courses`, `/courses/new` | Browse and create courses |
| `/students` | Enrolled students |
| `/assignments` | Assignments and rubrics |
| `/submissions` | Submit and run mock AI analysis |
| `/feedback` | Instructor feedback review and drafts |
| `/student-feedback` | Student view of finalized feedback |
| `/trace` | Live design-pattern trace (auto-refreshing) |

**Backend demo routes (`http://localhost:8080`)**

| Route | Purpose |
| --- | --- |
| `/demo` | One-link scripted walkthrough |
| `/demo/phase-2` | Course creation |
| `/demo/phase-3` | Submission workflow |
| `/demo/phase-4` | Mock AI analysis and Java code path |
| `/demo/phase-5` | Review and notification |
| `/trace` | Backend pattern trace |

## Demo Flow

A full presentation script lives in [`DEMO_SCRIPT.md`](DEMO_SCRIPT.md). The short version:

1. Start the backend and frontend.
2. Open the **Dashboard**.
3. Create a course and enroll students.
4. Create an assignment with a rubric.
5. Create a submission.
6. Run **Mock AI Analysis**.
7. Review and edit feedback, save/restore drafts.
8. Send **Final Feedback**.
9. View the **Student Feedback** page.
10. Open **Full Trace** to see every pattern that fired.

> Keep the backend running for the whole demo — restarting it clears the in-memory trace. Patterns are recorded during the submission, analysis, and feedback flow, so run those steps before opening the trace.

## Tech Stack

| Layer | Technologies |
| --- | --- |
| Backend | Java 21, Spring Boot 3.3, Maven, JUnit 5, in-memory repositories |
| Frontend | React 19, TypeScript, React Router 7, Vite 8 |
| Testing | JUnit 5 (backend), Vitest + React Testing Library (frontend) |

### Mock AI and mock test runner

The AI and code-execution pieces are deliberately simulated. `MockAIServiceAdapter` adapts a local `MockAIService` to the internal `AIClient` interface, and `MockCodeSandboxAdapter` adapts a local `MockCodeSandbox` to the internal `SandboxRunner` interface. The Java code path returns deterministic mock test results and never executes untrusted code.

## Project Structure

```
.
├── src/                # Spring Boot backend (all design-pattern implementations)
├── frontend/           # React + TypeScript instructor app
│   └── src/
│       ├── api/        # Typed REST clients
│       ├── app/        # App shell and routing
│       ├── pages/      # Feature pages
│       └── styles/     # Design tokens and global styles
├── docs/               # Diagrams and slides
├── figma/              # UI design references
├── DEMO_SCRIPT.md      # Presentation walkthrough
└── pom.xml
```

## Scope & Limitations

- Only Instructor and Student roles are modeled.
- Data is stored in memory for demo clarity.
- No external AI providers are called.
- Java code is evaluated by a deterministic mock test runner.
- The product surface is intentionally narrow so the design patterns stay the focus.

## Team

- Sriram Madduri
- Rakshitha Srinivasa
- Ankush Rai
