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
          This screen is coming soon. No placeholder data is shown until it is fully available.
        </p>
      </div>

      <section className="card empty-state">
        <span className="badge">Coming soon</span>
        <h3>Nothing to show yet</h3>
        <p>
          This screen will activate once the <code>{apiHint}</code> service is connected.
        </p>
      </section>
    </section>
  );
}
