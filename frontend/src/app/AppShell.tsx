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
  { to: '/feedback', label: 'Feedback Review' },
  { to: '/student-feedback', label: 'Student Feedback' },
  { to: '/trace', label: 'Full Trace' },
];

export function AppShell({ children }: AppShellProps) {
  return (
    <div className="app-layout">
      <header className="top-header">
        <div className="top-header__inner">
          <NavLink to="/" className="brand-link" aria-label="Course Management System home">
            <span className="brand-mark" aria-hidden="true">CM</span>
            <div>
              <h1>Course Management System</h1>
              <p>Design Patterns course project</p>
            </div>
          </NavLink>

          <nav className="sidebar__nav" aria-label="Primary navigation">
            {navItems.map((item) => (
              <NavLink key={item.to} to={item.to} end={item.end} aria-label={item.label}>
                {item.label}
              </NavLink>
            ))}
          </nav>

          <div className="top-header__actions" aria-label="Application status">
            <span className="view-pill">Instructor View</span>
            <span className="role-label">
              Role: <strong>Instructor</strong>
            </span>
            <span className="status-dot" aria-label="Backend-backed UI" />
          </div>
        </div>
      </header>

      <div className="app-frame">
        <main className="app-main">{children}</main>
      </div>
    </div>
  );
}
