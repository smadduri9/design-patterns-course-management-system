# Live Demo Script - 18 Design Patterns (~5 minutes)

Read this off your phone. Every count starts at zero, so you build the whole flow live.
The focus is the architecture: 18 design patterns working together. The Full Trace page (last item
in the sidebar) is the live activity log - the instructor-only view that proves each pattern fired.
You build the flow on the product screens, then open Full Trace at the end to show the evidence.

---

## 0:00 - Dashboard

SAY: "This is our Course Management System. It looks like a finished product, but the real story is
the architecture underneath - eighteen design patterns working together. Every number on screen is
zero, so I'm building this live, nothing is faked. Behind the scenes, an Observer pattern records
every step: objects subscribe to events, so each pattern that fires logs itself automatically. That
log lives on the Full Trace page - the instructor's audit view - and I'll open it at the very end to
prove every pattern ran."

DO:
- Show the dashboard at localhost:5173
- Point at the zero counts and the design-pattern list
- Point at the Full Trace tab at the bottom of the sidebar (we'll come back to it)

---

## 0:30 - Courses

SAY: "First the instructor creates a course. The Create button is wrapped in a Command object - it
packages an action as a reusable, undoable unit. The course-module-assignment structure is built with
the Composite pattern, which lets us treat a whole tree of objects as one."

DO:
- Click Courses in the sidebar
- Type Design Patterns CS501, click Create course
- Check 2 students, click Enroll selected students

---

## 1:15 - Assignments

SAY: "Now an assignment with a rubric. The assignment is assembled with the Builder pattern -
constructing a complex object step by step. The starter content comes from an Abstract Factory, which
creates families of related objects. And listing the rubric criteria uses the Iterator pattern, which
walks a collection without exposing how it's stored."

DO:
- Click Assignments, select the course
- Title: Adapter Pattern Essay
- Add rubric criterion: Correctness
- Click Create assignment

---

## 2:00 - Submissions

SAY: "The student submits. The entry point is a Facade - one simple call hiding a complex workflow -
which hands off to a Mediator that coordinates the steps so components never talk directly. The
submission runs through a Chain of Responsibility, a line of validators each passing it along. And its
status advances through the State pattern, where behavior changes with the object's state."

DO:
- Click Submissions, select course + assignment
- Pick a student, paste: "This essay explains how the Adapter pattern protects the domain from external services."
- Click Create submission
- Select it, click Run Mock AI Analysis

SAY: "Analysis just fired - and that's five more patterns. A Proxy checked a cache before doing
expensive work. A Factory Method picked the right analyzer for the submission type. The analysis
follows a Template Method - a fixed skeleton with type-specific steps. An Adapter wraps the AI service
so its API never leaks into our domain. A Strategy computes the grade with a swappable algorithm. And
the feedback is layered up with the Decorator pattern. Every one of those just logged itself to the
Full Trace page - we'll see the receipts at the end."

DO:
- Point at the analysis report (summary, rubric findings, suggested feedback, grade)

---

## 3:30 - Feedback Review

SAY: "The instructor reviews. This section is literally labeled 'Memento Drafts.' I'll save an edit,
save another, then restore the first - that's the Memento pattern, capturing and restoring state
without exposing internals."

DO:
- Click Feedback Review, select the submission
- Edit the feedback text, click Save Draft
- Edit again, click Save Draft
- Click Restore on the first draft

SAY: "Now I send it. An Observer fires a notification automatically, delivered through a Bridge - which
separates the message from the channel so each can change independently."

DO:
- Click Send Final Feedback
- Point at the grade and notification that appear

---

## 4:30 - Student Feedback + Full Trace

SAY: "From the student's side - here's the finalized feedback, the grade, and the notification."

DO:
- Click Student Feedback, select the same submission, show the card

SAY: "And here's the proof. Every pattern logged live - Command, Composite, Builder, Abstract Factory,
Iterator, Facade, Mediator, Chain of Responsibility, State, Proxy, Factory Method, Template Method,
Adapter, Strategy, Decorator, Memento, Observer, Bridge - sorted into creational, structural, and
behavioral. Eighteen design patterns, one working product, every one traceable end to end. That's the
project."

DO:
- Click Full Trace in the sidebar, land here and hold

---

## All 18 patterns, where they fire

- Courses: Command, Composite
- Assignments: Builder, Abstract Factory, Iterator
- Submissions: Facade, Mediator, Chain of Responsibility, State, Proxy, Factory Method, Template Method, Adapter, Strategy, Decorator
- Feedback: Memento, Observer, Bridge
