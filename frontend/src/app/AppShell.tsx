import type { ReactNode } from 'react';
import { NavLink } from 'react-router-dom';

type AppShellProps = {
  children: ReactNode;
};

export function AppShell({ children }: AppShellProps) {
  return (
    <div className="app-shell">
      <header className="app-header">
        <div>
          <p className="eyebrow">Interactive Instructor App</p>
          <h1>Course Management System</h1>
        </div>
        <nav aria-label="Primary navigation">
          <NavLink to="/">Shell</NavLink>
        </nav>
      </header>
      <main className="app-main">{children}</main>
    </div>
  );
}
