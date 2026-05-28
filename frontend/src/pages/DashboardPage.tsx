import { useEffect, useState } from 'react';

import { getDashboard } from '../api/dashboardApi';
import { getPatterns } from '../api/patternsApi';
import type { DashboardResponse, PatternResponse } from '../api/types';
import { TracePanel } from '../components/trace/TracePanel';

const countLabels: Record<keyof DashboardResponse['counts'], string> = {
  courses: 'Courses',
  students: 'Students',
  assignments: 'Assignments',
  submissions: 'Submissions',
  traceEvents: 'Trace Events',
};

export function DashboardPage() {
  const [dashboard, setDashboard] = useState<DashboardResponse | null>(null);
  const [patterns, setPatterns] = useState<PatternResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    Promise.all([getDashboard(), getPatterns()])
      .then(([dashboardResponse, patternResponse]) => {
        if (isMounted) {
          setDashboard(dashboardResponse);
          setPatterns(patternResponse);
          setError(null);
        }
      })
      .catch((caughtError: unknown) => {
        if (isMounted) {
          setError(caughtError instanceof Error ? caughtError.message : 'Unable to load dashboard');
        }
      })
      .finally(() => {
        if (isMounted) {
          setIsLoading(false);
        }
      });

    return () => {
      isMounted = false;
    };
  }, []);

  return (
    <div className="dashboard-layout">
      <section className="dashboard-main">
        <div className="hero-card">
          <p className="eyebrow">Instructor Dashboard</p>
          <h2>React-ready course management app</h2>
          <p>
            This screen renders instructor, count, pattern, and trace data returned by the
            Spring Boot <code>/api/app</code> endpoints.
          </p>
        </div>

        {isLoading ? <section className="card">Loading dashboard...</section> : null}
        {error ? <section className="card error-text">{error}</section> : null}
        {dashboard ? (
          <>
            <section className="card instructor-card" aria-labelledby="instructor-title">
              <p className="eyebrow">Seeded Instructor</p>
              <h2 id="instructor-title">{dashboard.instructor.name}</h2>
              <p>{dashboard.instructor.role}</p>
            </section>

            <section className="stat-grid" aria-label="Dashboard counts">
              {Object.entries(dashboard.counts).map(([key, value]) => (
                <article className="stat-card" key={key}>
                  <span>{countLabels[key as keyof DashboardResponse['counts']]}</span>
                  <strong>{value}</strong>
                </article>
              ))}
            </section>
          </>
        ) : null}

        <section className="card" aria-labelledby="patterns-title">
          <div className="section-heading">
            <div>
              <p className="eyebrow">Official Catalog</p>
              <h2 id="patterns-title">Design Patterns From Backend</h2>
            </div>
            <span className="badge">{patterns.length} patterns</span>
          </div>
          {patterns.length === 0 ? (
            <p className="muted">Pattern catalog has not loaded yet.</p>
          ) : (
            <div className="pattern-cloud">
              {patterns.map((pattern) => (
                <span key={pattern.key}>
                  {pattern.displayName} · {pattern.category}
                </span>
              ))}
            </div>
          )}
        </section>
      </section>

      <TracePanel />
    </div>
  );
}
