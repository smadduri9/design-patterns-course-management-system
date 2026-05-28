# design-patterns-course-management-system

A clean, modern web application for managing a university Design Patterns course. Built with React, TypeScript, Tailwind CSS, and React Router.

## Team Members

- Sriram Madduri
- Rakshitha Srinivasa
- Ankush Rai

## Features

### AI-Assisted Course Management
An academic learning management system designed specifically for Design Patterns courses, featuring:

- **Instructor Dashboard** — Course overview, submission tracking, AI-generated reports
- **Course Builder** — Create courses, modules, and assignments with custom rubrics
- **Submission Review** — Review student work with AI analysis and automated testing
- **Student Submission Portal** — Submit assignments and receive detailed feedback

### Design Pattern Trace Panel

A unique **right-side trace panel** that visually displays which design patterns are being used throughout the application workflow. Each action triggers pattern trace events categorized as:

- 🟢 **Creational** (Factory Method, Abstract Factory, Builder)
- 🔵 **Structural** (Adapter, Composite, Decorator)
- 🟣 **Behavioral** (Observer, State, Strategy)

## Navigation

The app includes 4 main screens accessible via React Router:

1. **Instructor Dashboard** (`/`) — Course cards, statistics, recent submissions
2. **Course Builder** (`/course-builder`) — Module list, assignment creation, rubric builder
3. **Submission Review** (`/submission-review`) — Student code, AI analysis, test results, grading
4. **Student Submission** (`/student`) — Assignment details, rubric preview, file upload

Use the **Instructor View** and **Student View** buttons in the header to switch between perspectives.

## Technical Stack

- **React** 18.3.1 with TypeScript
- **React Router** 7.13.0 for routing
- **Tailwind CSS** 4.x for styling
- **Lucide React** for icons
- **Radix UI** for accessible components

## Design System

### Typography
- **Work Sans** — Headings and display text
- **Inter** — Body text and UI labels
- **JetBrains Mono** — Code and monospace content

### Color Palette
- Primary: Indigo (`#4f46e5`)
- Accent: Purple (`#8b5cf6`)
- Background: Light gray (`#f8f9fb`)
- Cards: White (`#ffffff`)

### Pattern Colors
- Creational: Emerald
- Structural: Blue
- Behavioral: Purple

## Project Structure

```
src/
├── app/
│   ├── components/
│   │   ├── Header.tsx
│   │   ├── DesignPatternTrace.tsx
│   │   ├── InstructorDashboard.tsx
│   │   ├── CourseBuilder.tsx
│   │   ├── SubmissionReview.tsx
│   │   └── StudentSubmission.tsx
│   └── App.tsx
├── styles/
│   ├── fonts.css
│   ├── theme.css
│   ├── tailwind.css
│   └── index.css
└── guidelines/
    └── Guidelines.md
```

## Core Workflow

1. Instructor creates a course and modules
2. Instructor creates assignments with rubrics
3. Students submit PDF/text files or code
4. System analyzes submissions using AI
5. AI generates analysis report
6. Instructor reviews AI feedback and adds comments
7. Student receives final feedback via notification

## Core Entities

- User (Instructor/Student)
- Course
- Module
- Assignment
- Rubric
- Submission (PDF/Text or Code)
- AIAnalysisReport
- Grade
- Notification

## Visual Style

Clean, academic dashboard aesthetic with:
- Card-based layouts
- Rounded corners and subtle borders
- Blue/purple accent colors
- Clear typography hierarchy
- Professional but approachable design
- Prominent Design Pattern Trace Panel
