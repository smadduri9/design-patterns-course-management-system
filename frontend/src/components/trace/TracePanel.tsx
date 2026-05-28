import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import { getTrace } from '../../api/traceApi';
import type { TraceEventResponse } from '../../api/types';

type TracePanelProps = {
  title?: string;
  limit?: number;
  showFullTraceLink?: boolean;
};

export function TracePanel({
  title = 'Backend Pattern Trace',
  limit = 6,
  showFullTraceLink = true,
}: TracePanelProps) {
  const [events, setEvents] = useState<TraceEventResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    getTrace()
      .then((traceEvents) => {
        if (isMounted) {
          setEvents(traceEvents);
          setError(null);
        }
      })
      .catch((caughtError: unknown) => {
        if (isMounted) {
          setError(caughtError instanceof Error ? caughtError.message : 'Unable to load trace events');
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

  const visibleEvents = events.slice(0, limit);

  return (
    <aside className="card trace-panel" aria-labelledby="trace-panel-title">
      <div className="section-heading">
        <div>
          <p className="eyebrow">PatternTraceService</p>
          <h2 id="trace-panel-title">{title}</h2>
        </div>
        {showFullTraceLink ? <Link to="/trace">View all</Link> : null}
      </div>

      {isLoading ? <p className="muted">Loading backend trace events...</p> : null}
      {error ? <p className="error-text">{error}</p> : null}
      {!isLoading && !error && events.length === 0 ? (
        <p className="muted">No backend trace events yet.</p>
      ) : null}
      {!isLoading && !error && visibleEvents.length > 0 ? (
        <ol className="trace-list" aria-label="Backend trace events">
          {visibleEvents.map((event) => (
            <li key={`${event.timestamp}-${event.className}-${event.description}`}>
              <div className="trace-list__header">
                <span className="badge">{event.patternDisplayName}</span>
                <span className="trace-category">{event.category}</span>
              </div>
              <p>{event.description}</p>
              <small>
                {event.className} · {event.workflowStep}
              </small>
            </li>
          ))}
        </ol>
      ) : null}
    </aside>
  );
}
