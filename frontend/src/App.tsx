import { Route, Routes } from 'react-router-dom';

import { AppShell } from './app/AppShell';
import { AssignmentsPage } from './pages/AssignmentsPage';
import { CoursesPage } from './pages/CoursesPage';
import { DashboardPage } from './pages/DashboardPage';
import { FeedbackReviewPage } from './pages/FeedbackReviewPage';
import { FullTracePage } from './pages/FullTracePage';
import { StudentsPage } from './pages/StudentsPage';
import { StudentFeedbackPage } from './pages/StudentFeedbackPage';
import { SubmissionsPage } from './pages/SubmissionsPage';

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
        <Route path="/submissions" element={<SubmissionsPage />} />
        <Route path="/feedback" element={<FeedbackReviewPage />} />
        <Route path="/student-feedback" element={<StudentFeedbackPage />} />
        <Route path="/trace" element={<FullTracePage />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </AppShell>
  );
}
