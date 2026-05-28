import { useEffect, useState } from 'react';

import { getTrace } from '../api/traceApi';
import type { TraceEventResponse } from '../api/types';

export function FullTracePage() {
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

  return (
    <section className="card" aria-labelledby="full-trace-title">
      <div className="section-heading">
        <div>
          <p className="eyebrow">Full Trace</p>
          <h2 id="full-trace-title">Backend Pattern Trace Events</h2>
        </div>
        <span className="badge">{events.length} events</span>
      </div>

      {isLoading ? <p className="muted">Loading backend trace events...</p> : null}
      {error ? <p className="error-text">{error}</p> : null}
      {!isLoading && !error && events.length === 0 ? (
        <p className="muted">No backend trace events yet.</p>
      ) : null}
      {!isLoading && !error && events.length > 0 ? (
        <div className="trace-table" role="table" aria-label="Full backend trace events">
          <div className="trace-table__row trace-table__row--header" role="row">
            <span role="columnheader">Pattern</span>
            <span role="columnheader">Class</span>
            <span role="columnheader">Workflow Step</span>
            <span role="columnheader">Description</span>
          </div>
          {events.map((event) => (
            <div className="trace-table__row" role="row" key={`${event.timestamp}-${event.className}-${event.description}`}>
              <span role="cell">{event.patternDisplayName}</span>
              <span role="cell">{event.className}</span>
              <span role="cell">{event.workflowStep}</span>
              <span role="cell">{event.description}</span>
            </div>
          ))}
        </div>
      ) : null}
    </section>
  );
}
