import { FormEvent, useEffect, useMemo, useState } from 'react';

import { getPatterns } from '../api/patternsApi';
import { getTrace } from '../api/traceApi';
import type { PatternCategory, PatternResponse, TraceEventResponse } from '../api/types';

type TraceFilters = {
  category: '' | PatternCategory;
  pattern: string;
  workflowStep: string;
  search: string;
};

const emptyFilters: TraceFilters = {
  category: '',
  pattern: '',
  workflowStep: '',
  search: '',
};

export function FullTracePage() {
  const [events, setEvents] = useState<TraceEventResponse[]>([]);
  const [patterns, setPatterns] = useState<PatternResponse[]>([]);
  const [filters, setFilters] = useState<TraceFilters>(emptyFilters);
  const [appliedFilters, setAppliedFilters] = useState<TraceFilters>(emptyFilters);
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingPatterns, setIsLoadingPatterns] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [patternError, setPatternError] = useState<string | null>(null);

  const workflowSteps = useMemo(
    () => Array.from(new Set(events.map((event) => event.workflowStep).filter(Boolean))).sort(),
    [events],
  );

  useEffect(() => {
    let isMounted = true;

    getPatterns()
      .then((patternResponse) => {
        if (isMounted) {
          setPatterns(patternResponse);
          setPatternError(null);
        }
      })
      .catch((caughtError: unknown) => {
        if (isMounted) {
          setPatternError(caughtError instanceof Error ? caughtError.message : 'Unable to load pattern filters');
        }
      })
      .finally(() => {
        if (isMounted) {
          setIsLoadingPatterns(false);
        }
      });

    return () => {
      isMounted = false;
    };
  }, []);

  useEffect(() => {
    void loadTrace(appliedFilters);
  }, [appliedFilters]);

  async function loadTrace(nextFilters: TraceFilters) {
    setIsLoading(true);
    try {
      const traceEvents = await getTrace(nextFilters);
      setEvents(traceEvents);
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load trace events');
    } finally {
      setIsLoading(false);
    }
  }

  function handleApplyFilters(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setAppliedFilters(filters);
  }

  function handleClearFilters() {
    setFilters(emptyFilters);
    setAppliedFilters(emptyFilters);
  }

  return (
    <section className="page-stack" aria-labelledby="full-trace-title">
      <section className="hero-card hero-card--compact page-hero">
        <div>
          <p className="eyebrow">Full Trace</p>
          <h2 id="full-trace-title">Backend Design Pattern Trace</h2>
        </div>
        <p>Only official backend PatternTraceService events are shown here.</p>
      </section>

      <section className="card trace-filter-card" aria-labelledby="trace-filter-title">
        <div className="section-heading">
          <div>
            <p className="eyebrow">Evidence Filters</p>
            <h2 id="trace-filter-title">Filter backend events</h2>
          </div>
          <span className="badge">{events.length} events</span>
        </div>

        {patternError ? <p className="error-text">{patternError}</p> : null}

        <form className="trace-filter-form" onSubmit={handleApplyFilters}>
          <label className="field">
            Category
            <select
              value={filters.category}
              onChange={(event) => setFilters((current) => ({ ...current, category: event.target.value as TraceFilters['category'] }))}
            >
              <option value="">All categories</option>
              <option value="CREATIONAL">Creational</option>
              <option value="STRUCTURAL">Structural</option>
              <option value="BEHAVIORAL">Behavioral</option>
            </select>
          </label>

          <label className="field">
            Pattern
            <select
              value={filters.pattern}
              onChange={(event) => setFilters((current) => ({ ...current, pattern: event.target.value }))}
              disabled={isLoadingPatterns}
            >
              <option value="">All patterns</option>
              {patterns.map((pattern) => (
                <option value={pattern.key} key={pattern.key}>
                  {pattern.displayName}
                </option>
              ))}
            </select>
          </label>

          <label className="field">
            Workflow step
            <input
              list="workflow-steps"
              value={filters.workflowStep}
              onChange={(event) => setFilters((current) => ({ ...current, workflowStep: event.target.value }))}
              placeholder="Instructor review"
            />
            <datalist id="workflow-steps">
              {workflowSteps.map((workflowStep) => (
                <option value={workflowStep} key={workflowStep} />
              ))}
            </datalist>
          </label>

          <label className="field">
            Search
            <input
              value={filters.search}
              onChange={(event) => setFilters((current) => ({ ...current, search: event.target.value }))}
              placeholder="Adapter, sandbox, feedback..."
            />
          </label>

          <div className="button-row trace-filter-actions">
            <button type="submit">Apply Filters</button>
            <button type="button" onClick={handleClearFilters}>Clear Filters</button>
          </div>
        </form>
      </section>

      <section className="card trace-results-card" aria-labelledby="trace-results-title">
        <div className="section-heading">
          <div>
            <p className="eyebrow">PatternTraceService</p>
            <h2 id="trace-results-title">Backend trace evidence</h2>
          </div>
          <span className="badge">{events.length} events</span>
        </div>

        {isLoading ? <p className="muted">Loading backend trace events...</p> : null}
        {error ? <p className="error-text">{error}</p> : null}
        {!isLoading && !error && events.length === 0 ? (
          <p className="muted">No backend trace events match the current filters.</p>
        ) : null}
        {!isLoading && !error && events.length > 0 ? (
          <div className="trace-table trace-table--full" role="table" aria-label="Full backend trace events">
            <div className="trace-table__row trace-table__row--header" role="row">
              <span role="columnheader">Timestamp</span>
              <span role="columnheader">User Action</span>
              <span role="columnheader">Pattern</span>
              <span role="columnheader">Category</span>
              <span role="columnheader">Backend Class</span>
              <span role="columnheader">Workflow Step</span>
              <span role="columnheader">Description</span>
            </div>
            {events.map((event) => (
              <div className="trace-table__row" role="row" key={`${event.timestamp}-${event.className}-${event.description}`}>
                <span role="cell">{formatDateTime(event.timestamp)}</span>
                <span role="cell">{event.userAction}</span>
                <span role="cell"><span className="trace-card__pattern">{event.patternDisplayName}</span></span>
                <span role="cell">
                  <span className={`category-badge category-badge--${event.category.toLowerCase()}`}>{event.category}</span>
                </span>
                <span role="cell">{event.className}</span>
                <span role="cell">{event.workflowStep}</span>
                <span role="cell">{event.description}</span>
              </div>
            ))}
          </div>
        ) : null}
      </section>

      <section className="card trace-service-note" aria-label="PatternTraceService note">
        <p>
          PatternTraceService Events: only official backend events are shown here. Each row represents a design pattern
          execution triggered by instructor actions in the Spring Boot backend.
        </p>
      </section>
    </section>
  );
}

function formatDateTime(value: string) {
  return new Intl.DateTimeFormat(undefined, {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value));
}
