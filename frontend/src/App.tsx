import { Route, Routes } from 'react-router-dom';

import { AppShell } from './app/AppShell';
import { AssignmentsPage } from './pages/AssignmentsPage';
import { CoursesPage } from './pages/CoursesPage';
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
        <Route path="/courses" element={<CoursesPage />} />
        <Route path="/courses/new" element={<CoursesPage createFocused />} />
        <Route path="/students" element={<StudentsPage />} />
        <Route path="/assignments" element={<AssignmentsPage />} />
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
