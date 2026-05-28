import type { ReactNode } from 'react';
import { NavLink } from 'react-router-dom';

type AppShellProps = {
  children: ReactNode;
};

const navItems = [
  { to: '/', label: 'Dashboard', shortLabel: 'DB', end: true },
  { to: '/courses', label: 'Courses', shortLabel: 'CO' },
  { to: '/courses/new', label: 'Course Builder', shortLabel: 'CB' },
  { to: '/students', label: 'Students', shortLabel: 'ST' },
  { to: '/assignments', label: 'Assignments', shortLabel: 'AS' },
  { to: '/submissions', label: 'Submissions', shortLabel: 'SU' },
  { to: '/feedback', label: 'Feedback Review', shortLabel: 'FR' },
  { to: '/student-feedback', label: 'Student Feedback', shortLabel: 'SF' },
  { to: '/trace', label: 'Full Trace', shortLabel: 'FT' },
];

export function AppShell({ children }: AppShellProps) {
  return (
    <div className="app-layout">
      <aside className="app-sidebar" aria-label="Application sidebar">
        <div className="app-sidebar__brand">
          <NavLink to="/" className="brand-link" aria-label="Course Management System home">
            <span className="brand-mark" aria-hidden="true">CM</span>
            <div>
              <h1>Course Management System</h1>
              <p>Design Patterns course project</p>
            </div>
          </NavLink>
        </div>

        <nav className="sidebar__nav" aria-label="Primary navigation">
          {navItems.map((item) => (
            <NavLink key={item.to} to={item.to} end={item.end} aria-label={item.label}>
              <span aria-hidden="true">{item.shortLabel}</span>
              <strong>
                {item.label}
              </strong>
            </NavLink>
          ))}
        </nav>

        <div className="app-sidebar__footer">
          <span>Professor demo</span>
          <small>Backend-backed course workflows</small>
        </div>
      </aside>

      <div className="app-workspace">
        <header className="top-header">
          <div className="top-header__inner">
            <div className="top-header__title">
              <strong>Instructor Portal</strong>
              <span>Spring Boot API-driven UI</span>
            </div>

            <div className="top-header__actions" aria-label="Application status">
              <span className="status-dot" aria-label="Backend-backed UI" />
              <span className="view-pill">Instructor View</span>
              <span className="role-label">
                Role: <strong>Instructor</strong>
              </span>
            </div>
          </div>
        </header>

        <div className="app-frame">
          <main className="app-main">{children}</main>
        </div>
      </div>
    </div>
  );
}
