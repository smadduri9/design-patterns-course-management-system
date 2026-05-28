import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router';
import { Header } from './components/Header';
import { InstructorDashboard } from './components/InstructorDashboard';
import { CourseBuilder } from './components/CourseBuilder';
import { SubmissionReview } from './components/SubmissionReview';
import { StudentSubmission } from './components/StudentSubmission';

function AppContent() {
  const location = useLocation();
  const role = location.pathname === '/student' ? 'student' : 'instructor';

  return (
    <div className="min-h-screen bg-background">
      <Header role={role} />

      <Routes>
        <Route path="/" element={<InstructorDashboard />} />
        <Route path="/course-builder" element={<CourseBuilder />} />
        <Route path="/submission-review" element={<SubmissionReview />} />
        <Route path="/student" element={<StudentSubmission />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </div>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AppContent />
    </BrowserRouter>
  );
}