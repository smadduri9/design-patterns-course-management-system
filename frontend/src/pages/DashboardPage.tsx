import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

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

const demoFlow = [
  { to: '/courses', label: 'Courses', detail: 'Create a course and review roster data.' },
  { to: '/assignments', label: 'Assignments', detail: 'Build assignments and backend rubrics.' },
  { to: '/submissions', label: 'Submissions', detail: 'Create submissions and run Mock AI analysis.' },
  { to: '/feedback', label: 'Feedback Review', detail: 'Save drafts and send final instructor feedback.' },
  { to: '/student-feedback', label: 'Student Feedback', detail: 'Open the finalized student-facing view.' },
  { to: '/trace', label: 'Full Trace', detail: 'Filter official PatternTraceService evidence.' },
];

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
          <h2>Interactive instructor app</h2>
          <p>
            Follow the professor demo from course setup through assignments, submissions, Mock AI review,
            final feedback, and backend PatternTraceService evidence. Every value shown here comes from
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

        <section className="card" aria-labelledby="demo-flow-title">
          <div className="section-heading">
            <div>
              <p className="eyebrow">Professor Demo Flow</p>
              <h2 id="demo-flow-title">Recommended walkthrough</h2>
            </div>
            <span className="badge">Backend-only data</span>
          </div>
          <div className="demo-flow">
            {demoFlow.map((step, index) => (
              <Link to={step.to} key={step.to} aria-label={`Demo step ${index + 1}`}>
                <span>{String(index + 1).padStart(2, '0')}</span>
                <strong>{step.label}</strong>
                <small>{step.detail}</small>
              </Link>
            ))}
          </div>
        </section>

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
