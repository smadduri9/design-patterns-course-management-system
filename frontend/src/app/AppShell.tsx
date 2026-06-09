import type { ReactNode } from 'react';
import { NavLink } from 'react-router-dom';

type AppShellProps = {
  children: ReactNode;
};

type IconProps = { className?: string };

const Icon = {
  Dashboard: (p: IconProps) => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" {...p}>
      <rect x="3" y="3" width="7" height="9" rx="1.5" />
      <rect x="14" y="3" width="7" height="5" rx="1.5" />
      <rect x="14" y="12" width="7" height="9" rx="1.5" />
      <rect x="3" y="16" width="7" height="5" rx="1.5" />
    </svg>
  ),
  Courses: (p: IconProps) => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" {...p}>
      <path d="M4 5.5A1.5 1.5 0 0 1 5.5 4H11v16H5.5A1.5 1.5 0 0 1 4 18.5z" />
      <path d="M20 5.5A1.5 1.5 0 0 0 18.5 4H13v16h5.5A1.5 1.5 0 0 0 20 18.5z" />
    </svg>
  ),
  Builder: (p: IconProps) => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" {...p}>
      <path d="M12 3 3 7.5 12 12l9-4.5z" />
      <path d="M3 12 12 16.5 21 12" />
      <path d="M3 16.5 12 21l9-4.5" />
    </svg>
  ),
  Students: (p: IconProps) => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" {...p}>
      <circle cx="9" cy="8" r="3.2" />
      <path d="M3.5 19a5.5 5.5 0 0 1 11 0" />
      <path d="M16 5.2a3.2 3.2 0 0 1 0 6.1" />
      <path d="M17.5 14.2A5.5 5.5 0 0 1 20.5 19" />
    </svg>
  ),
  Assignments: (p: IconProps) => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" {...p}>
      <path d="M6 3h9l4 4v14a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1z" />
      <path d="M14 3v5h5" />
      <path d="M8.5 13h7M8.5 16.5h5" />
    </svg>
  ),
  Submissions: (p: IconProps) => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" {...p}>
      <path d="M12 3v11" />
      <path d="m8 9 4 4 4-4" />
      <path d="M5 18.5h14" />
    </svg>
  ),
  Feedback: (p: IconProps) => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" {...p}>
      <path d="M5 4h14a1 1 0 0 1 1 1v10a1 1 0 0 1-1 1H9l-4 4V5a1 1 0 0 1 1-1z" />
      <path d="M8.5 9h7M8.5 12h4" />
    </svg>
  ),
  StudentFeedback: (p: IconProps) => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" {...p}>
      <circle cx="12" cy="8" r="3.2" />
      <path d="M5.5 20a6.5 6.5 0 0 1 13 0" />
      <path d="m16.5 5.5 1.4 1.4L21 4" />
    </svg>
  ),
  Trace: (p: IconProps) => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" {...p}>
      <circle cx="5.5" cy="6" r="2" />
      <circle cx="18.5" cy="12" r="2" />
      <circle cx="5.5" cy="18" r="2" />
      <path d="M7.5 6h6a3 3 0 0 1 3 3v.5M16.5 13.5a3 3 0 0 1-3 3.5h-6" />
    </svg>
  ),
};

const navItems = [
  { to: '/', label: 'Dashboard', icon: Icon.Dashboard, end: true },
  { to: '/courses', label: 'Courses', icon: Icon.Courses },
  { to: '/courses/new', label: 'Course Builder', icon: Icon.Builder },
  { to: '/students', label: 'Students', icon: Icon.Students },
  { to: '/assignments', label: 'Assignments', icon: Icon.Assignments },
  { to: '/submissions', label: 'Submissions', icon: Icon.Submissions },
  { to: '/feedback', label: 'Feedback Review', icon: Icon.Feedback },
  { to: '/student-feedback', label: 'Student Feedback', icon: Icon.StudentFeedback },
  { to: '/trace', label: 'Full Trace', icon: Icon.Trace },
];

export function AppShell({ children }: AppShellProps) {
  return (
    <div className="app-layout">
      <aside className="app-sidebar" aria-label="Application sidebar">
        <div className="app-sidebar__brand">
          <NavLink to="/" className="brand-link" aria-label="Course Management System home">
            <span className="brand-mark" aria-hidden="true">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.9" strokeLinecap="round" strokeLinejoin="round">
                <path d="M3 7.5 12 3l9 4.5-9 4.5z" />
                <path d="M21 7.5v6" />
                <path d="M7 10v5.5c0 1.4 2.2 2.5 5 2.5s5-1.1 5-2.5V10" />
              </svg>
            </span>
            <div>
              <h1>Course Management System</h1>
              <p>Course Management Platform</p>
            </div>
          </NavLink>
        </div>

        <nav className="sidebar__nav" aria-label="Primary navigation">
          {navItems.map((item) => {
            const IconComponent = item.icon;
            return (
              <NavLink key={item.to} to={item.to} end={item.end} aria-label={item.label}>
                <span aria-hidden="true">
                  <IconComponent />
                </span>
                <strong>{item.label}</strong>
              </NavLink>
            );
          })}
        </nav>

        <div className="app-sidebar__footer">
          <span>Course Management System</span>
          <small>Version 1.0 &middot; Instructor workspace</small>
        </div>
      </aside>

      <div className="app-workspace">
        <header className="top-header">
          <div className="top-header__inner">
            <div className="top-header__title">
              <strong>Instructor Portal</strong>
              <span>Courses, submissions, and AI-assisted feedback</span>
            </div>

            <div className="top-header__actions" aria-label="Application status">
              <span className="status-dot" aria-label="System online" />
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
