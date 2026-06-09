import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import { getDashboard } from '../api/dashboardApi';
import { getPatterns } from '../api/patternsApi';
import type { DashboardResponse, PatternResponse } from '../api/types';

const countLabels: Record<keyof DashboardResponse['counts'], string> = {
  courses: 'Courses',
  students: 'Students',
  assignments: 'Assignments',
  submissions: 'Submissions',
  traceEvents: 'Trace Events',
};

const demoFlow = [
  { to: '/courses', label: 'Courses', detail: 'Create a course and manage the enrolled roster.' },
  { to: '/assignments', label: 'Assignments', detail: 'Build assignments with structured rubrics.' },
  { to: '/submissions', label: 'Submissions', detail: 'Collect submissions and run AI analysis.' },
  { to: '/feedback', label: 'Feedback Review', detail: 'Draft, refine, and send instructor feedback.' },
  { to: '/student-feedback', label: 'Student Feedback', detail: 'Open the finalized student-facing view.' },
  { to: '/trace', label: 'Full Trace', detail: 'Inspect the live design-pattern activity log.' },
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
    <section className="dashboard-main">
        <div className="hero-card page-hero">
          <div>
            <p className="eyebrow">Instructor Dashboard</p>
            <h2>Manage your courses end to end</h2>
          </div>
          <p>
            Set up courses, build assignments, collect student submissions, run AI-assisted analysis, and
            deliver final feedback &mdash; all in one workspace.
          </p>
        </div>

        {isLoading ? <section className="card">Loading dashboard...</section> : null}
        {error ? <section className="card error-text">{error}</section> : null}
        {dashboard ? (
          <>
            <section className="card instructor-card" aria-labelledby="instructor-title">
              <p className="eyebrow">Signed in as</p>
              <h2 id="instructor-title">{dashboard.instructor.name}</h2>
              <p>{dashboard.instructor.role}</p>
            </section>

            <section className="stat-grid" aria-label="Dashboard counts">
              {Object.entries(dashboard.counts).map(([key, value]) => (
                <article className="stat-card" key={key}>
                  <div className="stat-card__topline">
                    <span>{countLabels[key as keyof DashboardResponse['counts']]}</span>
                    <i aria-hidden="true">{String(countLabels[key as keyof DashboardResponse['counts']][0])}</i>
                  </div>
                  <strong>{value}</strong>
                  <small>Live total</small>
                </article>
              ))}
            </section>
          </>
        ) : null}

        <section className="card" aria-labelledby="demo-flow-title">
          <div className="section-heading">
            <div>
              <p className="eyebrow">Workflow</p>
              <h2 id="demo-flow-title">Get started</h2>
            </div>
            <span className="badge">Live data</span>
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
              <p className="eyebrow">Architecture</p>
              <h2 id="patterns-title">Design Patterns In Use</h2>
            </div>
            <span className="badge">{patterns.length} patterns</span>
          </div>
          {patterns.length === 0 ? (
            <p className="muted">Pattern catalog has not loaded yet.</p>
          ) : (
            <div className="pattern-cloud">
              {patterns.map((pattern) => (
                <span className="pattern-chip" key={pattern.key}>
                  {pattern.displayName} · {pattern.category}
                </span>
              ))}
            </div>
          )}
        </section>
    </section>
  );
}
