import type { ReactNode } from 'react';
import { NavLink } from 'react-router-dom';

type AppShellProps = {
  children: ReactNode;
};

const navItems = [
  { to: '/', label: 'Dashboard', end: true },
  { to: '/courses', label: 'Courses' },
  { to: '/courses/new', label: 'Course Builder' },
  { to: '/students', label: 'Students' },
  { to: '/assignments', label: 'Assignments' },
  { to: '/submissions', label: 'Submissions' },
  { to: '/student-feedback', label: 'Student Feedback' },
  { to: '/trace', label: 'Full Trace' },
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
          </div>
        </div>
        <nav className="sidebar__nav" aria-label="Primary navigation">
          {navItems.map((item) => (
            <NavLink key={item.to} to={item.to} end={item.end}>
              {item.label}
            </NavLink>
          ))}
        </nav>
      </aside>

      <div className="app-frame">
        <header className="top-header">
          <div>
            <p className="eyebrow">Interactive Instructor App</p>
            <h1>Course Management System</h1>
          </div>
          <span className="badge">Backend-backed UI</span>
        </header>
        <main className="app-main">{children}</main>
      </div>
    </div>
  );
}
