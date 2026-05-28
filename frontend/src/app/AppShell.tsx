import type { ReactNode } from 'react';
import { NavLink } from 'react-router-dom';

type AppShellProps = {
  children: ReactNode;
};

const navItems = [
  { to: '/', label: 'Dashboard', step: '01', end: true },
  { to: '/courses', label: 'Courses', step: '02' },
  { to: '/courses/new', label: 'Course Builder', step: '03' },
  { to: '/students', label: 'Students', step: '04' },
  { to: '/assignments', label: 'Assignments', step: '05' },
  { to: '/submissions', label: 'Submissions', step: '06' },
  { to: '/feedback', label: 'Feedback Review', step: '07' },
  { to: '/student-feedback', label: 'Student Feedback', step: '08' },
  { to: '/trace', label: 'Full Trace', step: '09' },
];

export function AppShell({ children }: AppShellProps) {
  return (
    <div className="app-layout">
      <aside className="sidebar" aria-label="Application sidebar">
        <div className="sidebar__brand">
          <span className="brand-mark">CM</span>
          <div>
            <p className="eyebrow">Instructor App</p>
            <strong>Course Manager</strong>
            <small>Backend-backed demo</small>
          </div>
        </div>
        <nav className="sidebar__nav" aria-label="Primary navigation">
          {navItems.map((item) => (
            <NavLink key={item.to} to={item.to} end={item.end} aria-label={item.label}>
              <span aria-hidden="true">{item.step}</span>
              <strong>{item.label}</strong>
            </NavLink>
          ))}
        </nav>
      </aside>

      <div className="app-frame">
        <header className="top-header">
          <div>
            <p className="eyebrow">Interactive Instructor App</p>
            <h1>Course Management System</h1>
            <p>Backend APIs drive every count, workflow record, and PatternTraceService event.</p>
          </div>
          <span className="badge">Backend-backed UI</span>
        </header>
        <main className="app-main">{children}</main>
      </div>
    </div>
  );
}
