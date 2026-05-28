## 1. Current App Routes

All routes are defined in `frontend/src/App.tsx` and wrapped by `frontend/src/app/AppShell.tsx`.

### `/`
- Component: `DashboardPage` in `frontend/src/pages/DashboardPage.tsx`
- APIs:
  - `GET /api/app/dashboard`
  - `GET /api/app/patterns`
  - `GET /api/app/trace` via `TracePanel`
- Purpose: instructor landing page with backend counts, seeded instructor, demo flow links, pattern catalog, and trace panel.
- Status: complete for current backend-backed demo dashboard.

### `/courses`
- Component: `CoursesPage` in `frontend/src/pages/CoursesPage.tsx`
- APIs:
  - `GET /api/app/courses`
  - `POST /api/app/courses`
  - `GET /api/app/courses/{courseId}`
  - `GET /api/app/courses/{courseId}/roster`
  - `POST /api/app/courses/{courseId}/enrollments`
  - `GET /api/app/students`
- Purpose: create courses, select a course, view roster, enroll backend students.
- Status: complete for current course and roster workflow.

### `/courses/new`
- Component: `CoursesPage createFocused`
- APIs: same as `/courses`
- Purpose: “Course Builder” entry point, but currently reuses the same course/roster page and only changes hero copy.
- Status: partial as a distinct builder screen; functionally complete for creating a course.

### `/students`
- Component: `StudentsPage` in `frontend/src/pages/StudentsPage.tsx`
- APIs:
  - `GET /api/app/students`
- Purpose: read-only seeded student roster/catalog.
- Status: partial if Figma expects roster management here; enrollment actually lives on `/courses`.

### `/assignments`
- Component: `AssignmentsPage` in `frontend/src/pages/AssignmentsPage.tsx`
- APIs:
  - `GET /api/app/courses`
  - `GET /api/app/courses/{courseId}/assignments`
  - `POST /api/app/courses/{courseId}/assignments`
- Purpose: choose course, list assignments, create assignment with rubric criteria.
- Status: complete for assignment/rubric creation.

### `/submissions`
- Component: `SubmissionsPage` in `frontend/src/pages/SubmissionsPage.tsx`
- APIs:
  - `GET /api/app/courses`
  - `GET /api/app/students`
  - `GET /api/app/courses/{courseId}/assignments`
  - `GET /api/app/assignments/{assignmentId}/submissions`
  - `POST /api/app/assignments/{assignmentId}/submissions`
  - `GET /api/app/submissions/{submissionId}`
  - `POST /api/app/submissions/{submissionId}/analyze`
  - `GET /api/app/trace` via `TracePanel`
- Purpose: create submissions, list submissions, view selected submission, run Mock AI Analysis, show report and trace.
- Status: complete for current backend, with one known backend limitation: content preview says it is not returned by current detail API.

### `/feedback`
- Component: `FeedbackReviewPage` in `frontend/src/pages/FeedbackReviewPage.tsx`
- APIs:
  - `GET /api/app/courses`
  - `GET /api/app/courses/{courseId}/assignments`
  - `GET /api/app/assignments/{assignmentId}/submissions`
  - `GET /api/app/submissions/{submissionId}`
  - `GET /api/app/submissions/{submissionId}/feedback-drafts`
  - `POST /api/app/submissions/{submissionId}/feedback-drafts`
  - `POST /api/app/submissions/{submissionId}/feedback-drafts/restore`
  - `POST /api/app/submissions/{submissionId}/final-feedback`
  - `GET /api/app/trace` via `TracePanel`
- Purpose: select analyzed submission, save/restore feedback drafts, send final feedback, show resulting final feedback and trace.
- Status: complete for current instructor feedback workflow.

### `/student-feedback`
- Component: `StudentFeedbackPage` in `frontend/src/pages/StudentFeedbackPage.tsx`
- APIs:
  - `GET /api/app/courses`
  - `GET /api/app/courses/{courseId}/assignments`
  - `GET /api/app/assignments/{assignmentId}/submissions`
  - `GET /api/app/submissions/{submissionId}/student-feedback`
- Purpose: select a submission and render finalized student-facing feedback if available.
- Status: complete for finalized feedback viewing; intentionally shows unavailable state instead of mocked feedback.

### `/trace`
- Component: `FullTracePage` in `frontend/src/pages/FullTracePage.tsx`
- APIs:
  - `GET /api/app/patterns`
  - `GET /api/app/trace`
  - `GET /api/app/trace?category=&pattern=&workflowStep=&search=`
- Purpose: full backend PatternTraceService evidence page with filters and pseudo-table results.
- Status: complete for trace browsing/filtering.

### `*`
- Component: inline `NotFound` in `frontend/src/App.tsx`
- APIs: none
- Purpose: reserved route placeholder.
- Status: placeholder.

## 2. Current Component Structure

### Shell/Layout
- `frontend/src/app/AppShell.tsx`
  - Fixed top header.
  - Brand link.
  - Horizontal nav using `NavLink`.
  - Status/right-side header affordances.
  - Main app frame.
- There is no separate `Header`, `Sidebar`, `PageHeader`, or layout component folder.
- The class `sidebar__nav` still names the primary nav, but visually it is now top navigation.

### Trace Components
- `frontend/src/components/trace/TracePanel.tsx`
  - Fetches backend trace events itself.
  - Used by Dashboard, Submissions, Feedback Review.
  - Displays loading/error/empty/list states.
  - Renders category-colored trace cards and category legend.
- `FullTracePage.tsx`
  - Owns full trace filters and trace table.
  - Not extracted into reusable filter/table components.

### Shared UI Components
There are no React shared card/table/form/button components. Shared UI is CSS-only through classes in `frontend/src/styles/global.css`, including:
- `.card`
- `.hero-card`
- `.stat-card`
- `.badge`
- `.field`
- `.button-row`
- `.course-card`
- `.submission-card`
- `.assignment-card`
- `.student-card`
- `.compact-list`
- `.detail-list`
- `.trace-table`
- `.trace-panel`

### Page-Specific Components
- `SubmissionsPage.tsx` contains inline `AnalysisReport`.
- `StudentsPage.tsx` contains local `initials()`.
- `FullTracePage.tsx` contains local filter state and `formatDateTime()`.
- `FeedbackReviewPage.tsx` and `StudentFeedbackPage.tsx` each contain local `formatDateTime()`.
- `PendingApiPage.tsx` exists but is not routed.

## 3. Current Data Flow

### Dashboard
- Initial load:
  - `Promise.all([getDashboard(), getPatterns()])`
  - `TracePanel` separately calls `getTrace()`
- User actions:
  - Demo flow links navigate to real routes.
  - “View all” in trace panel links to `/trace`.
- States:
  - Loading: dashboard loading card; trace loading text.
  - Error: dashboard error card; trace error text.
  - Empty: pattern catalog says not loaded/empty; trace says no backend trace events.
  - Loaded: instructor, counts, pattern chips, trace events.

### Courses/Roster
- Initial load:
  - `getCourses()`
  - Auto-selects first course if present.
- Course selection:
  - `getCourse(courseId)`
  - `getCourseRoster(courseId)`
  - `getStudents()`
- User actions:
  - Create course.
  - Select course.
  - Toggle available students.
  - Enroll selected students.
  - Navigate to assignments.
- States:
  - Loading courses.
  - Loading roster.
  - Error banner.
  - Empty course list.
  - Empty enrolled students.
  - Empty available students when all are enrolled.
  - Loaded course list and roster panel.

### Assignments/Rubrics
- Initial load:
  - `getCourses()`
  - Auto-selects first course.
- Course selection:
  - `getCourseAssignments(courseId)`
- User actions:
  - Select course.
  - Fill assignment fields.
  - Add/remove rubric criteria.
  - Create assignment.
  - Navigate to submissions.
- States:
  - Loading courses/assignments.
  - Error banner.
  - No courses.
  - No assignments for selected course.
  - Form validation errors.
  - Loaded assignment cards and creation form.

### Submissions/Mock AI Analysis
- Initial load:
  - `Promise.all([getCourses(), getStudents()])`
  - Auto-selects first course.
- Course selection:
  - `getCourseAssignments(courseId)`
  - Auto-selects first assignment.
- Assignment selection:
  - `getAssignmentSubmissions(assignmentId)`
- Submission selection:
  - `getSubmission(submissionId)`
- User actions:
  - Select course.
  - Select assignment.
  - Select submission.
  - Create submission.
  - Run Mock AI Analysis.
- After analysis:
  - Reloads submissions.
  - Reloads selected submission detail.
  - Refreshes `TracePanel` by changing `traceRefreshKey`.
- States:
  - Loading courses/assignments/submissions/detail.
  - Error banner.
  - No courses.
  - No assignments.
  - No submissions.
  - No selected submission.
  - Detail loaded without report, showing “Run Mock AI Analysis”.
  - Detail loaded with report, showing analysis report.

### Feedback Review
- Initial load:
  - `getCourses()`
  - Auto-selects first course.
- Course selection:
  - `getCourseAssignments(courseId)`
  - Auto-selects first assignment.
- Assignment selection:
  - `getAssignmentSubmissions(assignmentId)`
  - Does not auto-select a submission.
- Submission selection:
  - `Promise.all([getSubmission(submissionId), getFeedbackDrafts(submissionId)])`
- User actions:
  - Select course.
  - Select assignment.
  - Select submission.
  - Type feedback.
  - Save draft.
  - Restore draft.
  - Send final feedback.
- After final feedback:
  - Reloads submission detail.
  - Reloads submissions preserving selection.
  - Refreshes `TracePanel`.
- States:
  - Loading courses/assignments/submissions/feedback.
  - Error banner.
  - No assignments.
  - No submissions.
  - No selected submission.
  - Feedback unavailable until analysis/review/finalized state.
  - Draft list empty or loaded.
  - Final result loaded.

### Student Feedback
- Initial load:
  - `getCourses()`
  - Auto-selects first course.
- Course selection:
  - `getCourseAssignments(courseId)`
  - Auto-selects first assignment.
- Assignment selection:
  - `getAssignmentSubmissions(assignmentId)`
  - Auto-selects first submission.
- Submission selection:
  - `getStudentFeedback(submissionId)`
- User actions:
  - Select course.
  - Select assignment.
  - Select submission.
- States:
  - Loading courses/assignments/submissions/feedback.
  - Error banner for course/assignment/submission loading.
  - Feedback unavailable message for unfinalized or missing student feedback.
  - Loaded final feedback, grade, AI summary, optional notification.

### Full Trace
- Initial load:
  - `getPatterns()`
  - `getTrace(emptyFilters)`
- User actions:
  - Change category/pattern/workflow/search filters.
  - Apply filters.
  - Clear filters.
- States:
  - Loading patterns.
  - Loading trace.
  - Pattern filter error.
  - Trace error.
  - Empty trace results.
  - Loaded trace table.

## 4. Current CSS/Style Structure

### Files
- `frontend/src/styles/tokens.css`
  - Design tokens: colors, radii, spacing, shadows, fonts.
- `frontend/src/styles/global.css`
  - Everything else: app shell, nav, page layout, cards, trace, forms, buttons, grids, lists, responsive rules.
- There are no CSS modules, Tailwind classes in app code, or component-scoped style files.

### Layout Styling
- App shell/topbar:
  - `.app-layout`
  - `.top-header`
  - `.top-header__inner`
  - `.brand-link`
  - `.brand-mark`
  - `.sidebar__nav`
  - `.top-header__actions`
  - `.app-frame`
  - `.app-main`
- Page layout:
  - `.page-stack`
  - `.dashboard-layout`
  - `.dashboard-main`
  - `.courses-layout`
  - `.assignments-layout`
  - `.submissions-layout`
  - `.feedback-layout`
- Sticky panels:
  - `.trace-panel`
  - `.roster-panel`
  - `.submission-detail-panel`

### Component Styling
- Cards:
  - `.card`, `.hero-card`, `.stat-card`, `.student-card`, `.course-card`, `.assignment-card`, `.submission-card`, `.analysis-report`, `.criterion-card`
- Forms:
  - global `input`, `select`, `textarea`, `button`
  - `.field`, `.inline-form`, `.assignment-form`, `.form-grid`, `.checkbox-list`, `.button-row`
- Tables/lists:
  - `.trace-table`, `.trace-table__row`
  - `.compact-list`
  - `.course-list`, `.assignment-list`, `.submission-list`
- Badges/labels:
  - `.badge`
  - `.category-badge`
  - `.eyebrow`
  - `.muted`
  - `.error-text`

### Class Naming Patterns
- Mostly BEM-ish for shell/trace: `.top-header__inner`, `.trace-list__header`, `.trace-table__row`.
- Semantic page classes: `.dashboard-layout`, `.courses-layout`, `.feedback-layout`.
- Generic reusable classes: `.card`, `.field`, `.badge`, `.muted`.
- Some stale naming remains: `.sidebar__nav` is now top navigation, not a sidebar.

### Messy/Inconsistent Areas
- One large `global.css` owns all visual behavior; no separation between shell, primitives, page layouts, and workflow-specific styling.
- No React-level design system components, so pages repeat raw cards/forms/buttons manually.
- Several pages duplicate course/assignment/submission cascading selection logic.
- Trace has two implementations: compact `TracePanel` plus full-page `FullTracePage`, with no shared trace row/filter primitives.
- Analysis/report UI is duplicated or abbreviated between Submissions, Feedback Review, and Student Feedback.
- `/courses/new` is not a true Course Builder screen; it reuses `CoursesPage`.
- `PendingApiPage.tsx` is unused.
- CSS class `.sidebar__nav` no longer matches the visual topbar implementation.
- Some global element styles (`button`, `input`, `select`, `textarea`) may make future specialized controls harder to isolate.

## 5. Design Brief For Figma

### Exact Pages To Design
Design these as the actual app pages/routes:

- Dashboard: `/`
- Courses and Roster: `/courses`
- Course Builder / New Course variant: `/courses/new`
- Students: `/students`
- Assignments and Rubric Builder: `/assignments`
- Submissions and Mock AI Analysis: `/submissions`
- Feedback Review: `/feedback`
- Student Feedback: `/student-feedback`
- Full Trace: `/trace`
- Optional fallback/empty route placeholder: `*`

### Exact Components To Design
Reusable shell:
- Fixed top header
- Brand/logo area
- Horizontal route nav
- Active nav state
- Role/status area
- Main content container

Reusable primitives:
- Page hero/header card
- Section card
- Stat card
- Badge
- Category badge
- Primary button
- Secondary/outline button
- Disabled button
- Text input
- Select
- Textarea
- Checkbox row
- Empty state
- Error state
- Loading state
- Selectable list card
- Detail key/value panel
- Compact list row
- Trace event card
- Trace side panel
- Trace full table row
- Filter form group

Workflow-specific components:
- Dashboard stat grid
- Demo flow cards
- Pattern catalog chips
- Course card
- Roster enrolled student row
- Available student checkbox row
- Student avatar card
- Assignment card
- Rubric criterion editor card
- Submission card
- Submission detail panel
- Mock AI analysis report
- Rubric finding row
- Test result row
- Feedback draft row
- Final feedback result card
- Student feedback result card
- Trace filter bar
- Full trace table

### Real Page Hierarchy
Use the actual route hierarchy:

- App shell
  - Fixed top header
  - Main frame
    - Page hero
    - Error/loading states near top
    - Page-specific content grid
    - Optional right/sticky panel

Key page layout patterns:
- Dashboard: main dashboard column + trace panel.
- Courses: left create/list stack + right roster panel.
- Students: hero + responsive student card grid.
- Assignments: left assignment list + right assignment/rubric form.
- Submissions: left selectors/list/create form + right detail/trace stack.
- Feedback Review: left selection/drafts + right analysis/final result/trace stack.
- Student Feedback: left selection + right final response.
- Full Trace: hero + filter card + full-width trace table.

### Required States
Figma should include these states explicitly:

- Loading
  - Dashboard loading
  - Courses loading
  - Roster loading
  - Assignments loading
  - Submissions loading
  - Submission detail loading
  - Feedback loading
  - Trace loading
- Error
  - Generic backend error banner/card
  - Pattern filter error on Full Trace
  - Student feedback unavailable message
- Empty
  - No backend courses
  - No enrolled students
  - All students already enrolled
  - No assignments for course
  - No submissions for assignment
  - No trace events
  - No trace results after filters
  - No rubric findings/test results returned
  - No finalized feedback
- Loaded
  - Normal populated page
  - Selected item state for courses/submissions
  - Disabled action states
  - In-progress action labels: creating, enrolling, analyzing, saving, finalizing
  - Final feedback sent state
  - Trace category states: creational, structural, behavioral

### Layout Constraints
- Desktop-first width currently maxes at `1280px`.
- Fixed topbar is `4rem` high.
- Main content starts below topbar.
- Common two-column layouts use a main column plus `minmax(340px/360px, 0.9fr)` side panel.
- Responsive breakpoint currently collapses major grids at `920px`.
- Trace panel is sticky, not globally fixed, and appears only on Dashboard/Submissions/Feedback Review.
- Full Trace needs horizontal overflow for the seven-column trace table.
- Avoid designs that require data not returned by APIs, especially:
  - Student email
  - Submission content preview
  - Fake recent notifications
  - Fake trace events
  - Fake dashboard counts

### Reusable Design System Components
Minimum Figma component set:
- AppTopHeader
- TopNavItem
- PageHero
- Card
- SectionHeader
- StatCard
- Badge
- CategoryBadge
- ButtonPrimary
- ButtonSecondary
- FormField
- SelectField
- TextAreaField
- CheckboxRow
- SelectableCard
- DetailList
- EmptyState
- ErrorBanner
- LoadingBlock
- TracePanel
- TraceEventCard
- TraceFilterForm
- TraceTable
- AnalysisReport
- FeedbackDraftListItem

## 6. Safe Visual Implementation Strategy For Cursor

### Files To Change Later
Visual implementation should mostly touch:

- `frontend/src/app/AppShell.tsx`
- `frontend/src/styles/tokens.css`
- `frontend/src/styles/global.css`
- `frontend/src/components/trace/TracePanel.tsx`
- `frontend/src/pages/DashboardPage.tsx`
- `frontend/src/pages/CoursesPage.tsx`
- `frontend/src/pages/StudentsPage.tsx`
- `frontend/src/pages/AssignmentsPage.tsx`
- `frontend/src/pages/SubmissionsPage.tsx`
- `frontend/src/pages/FeedbackReviewPage.tsx`
- `frontend/src/pages/StudentFeedbackPage.tsx`
- `frontend/src/pages/FullTracePage.tsx`

If introducing shared UI components, add under:
- `frontend/src/components/`

Good candidates:
- `components/ui/Card.tsx`
- `components/ui/Button.tsx`
- `components/ui/Badge.tsx`
- `components/ui/FormField.tsx`
- `components/layout/PageHero.tsx`
- `components/trace/TraceEventCard.tsx`
- `components/trace/TraceTable.tsx`

### Files Not To Touch For Visual Work
Do not change these unless the backend contract intentionally changes:
- `frontend/src/api/client.ts`
- `frontend/src/api/dashboardApi.ts`
- `frontend/src/api/coursesApi.ts`
- `frontend/src/api/assignmentsApi.ts`
- `frontend/src/api/submissionsApi.ts`
- `frontend/src/api/feedbackApi.ts`
- `frontend/src/api/traceApi.ts`
- `frontend/src/api/patternsApi.ts`
- `frontend/src/api/usersApi.ts`
- Backend Java source
- Backend tests

### Avoid Breaking API-Driven Behavior
- Preserve all existing API function calls.
- Preserve all `useEffect` dependency flows unless intentionally refactoring with tests.
- Preserve selected course/assignment/submission state behavior.
- Keep loading/error/empty states.
- Keep form validation currently done in page components.
- Keep `traceRefreshKey` behavior after analysis/final feedback.

### Avoid Fake Data
- Do not copy hardcoded Figma prototype arrays into app pages.
- Do not invent counts, students, courses, submissions, notifications, reports, grades, or trace events.
- If a design shows data unavailable from the backend, render an empty/unavailable state or omit that element.
- Keep labels honest, e.g. “Not returned by current backend detail API.”

### Avoid Replacing Working Pages With Hardcoded Figma Code
- Treat Figma output as layout/style guidance only.
- Port visual structure incrementally around existing state and API calls.
- Prefer extracting reusable presentational components that accept existing backend data as props.
- Keep route components as the workflow owners unless doing a deliberate refactor.
- After each phase, run:
  - `cd frontend && npm test`
  - `cd frontend && npm run build`
  - `mvn test`

### Suggested Implementation Order
1. Normalize design tokens and global primitives.
2. Extract shell/page/card/button/badge/form primitives.
3. Refactor Dashboard and Trace first because they establish visual language.
4. Refactor Courses/Assignments forms and selectable cards.
5. Refactor Submissions/Feedback workflows carefully, preserving action behavior.
6. Refactor Full Trace table/filter UI last.
7. Only then clean up naming like `.sidebar__nav` and remove unused `PendingApiPage.tsx` if desired.