import { Route, Routes } from 'react-router-dom';

import { AppShell } from './app/AppShell';
import { DashboardPage } from './pages/DashboardPage';
import { FullTracePage } from './pages/FullTracePage';
import { PendingApiPage } from './pages/PendingApiPage';
import { StudentsPage } from './pages/StudentsPage';

function NotFound() {
  return (
    <section className="card" aria-labelledby="not-found-title">
      <h2 id="not-found-title">Route placeholder</h2>
      <p>This route is reserved for a future frontend phase.</p>
    </section>
  );
}

export function App() {
  return (
    <AppShell>
      <Routes>
        <Route path="/" element={<DashboardPage />} />
        <Route
          path="/courses"
          element={<PendingApiPage title="Courses" eyebrow="Courses" apiHint="/api/app/courses" />}
        />
        <Route
          path="/courses/new"
          element={<PendingApiPage title="Course Builder" eyebrow="Course Builder" apiHint="/api/app/courses" />}
        />
        <Route path="/students" element={<StudentsPage />} />
        <Route
          path="/assignments"
          element={<PendingApiPage title="Assignments" eyebrow="Assignments" apiHint="/api/app/courses/{courseId}/assignments" />}
        />
        <Route
          path="/submissions"
          element={<PendingApiPage title="Submissions" eyebrow="Submissions" apiHint="/api/app/assignments/{assignmentId}/submissions" />}
        />
        <Route
          path="/student-feedback"
          element={<PendingApiPage title="Student Feedback" eyebrow="Student Feedback" apiHint="/api/app/submissions/{submissionId}/student-feedback" />}
        />
        <Route path="/trace" element={<FullTracePage />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </AppShell>
  );
}
