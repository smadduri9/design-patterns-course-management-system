type PendingApiPageProps = {
  title: string;
  eyebrow: string;
  apiHint: string;
};

export function PendingApiPage({ title, eyebrow, apiHint }: PendingApiPageProps) {
  return (
    <section className="page-stack" aria-labelledby="pending-title">
      <div className="hero-card hero-card--compact">
        <p className="eyebrow">{eyebrow}</p>
        <h2 id="pending-title">{title}</h2>
        <p>
          Backend API pending for this frontend screen. This route is intentionally not rendering placeholder business data.
        </p>
      </div>

      <section className="card empty-state">
        <span className="badge">Backend API pending</span>
        <h3>No mock records shown</h3>
        <p>
          This screen will activate after the corresponding <code>{apiHint}</code> API is available to this frontend phase.
          Until then, React will not synthesize courses, assignments, submissions, feedback, or trace events.
        </p>
      </section>
    </section>
  );
}
