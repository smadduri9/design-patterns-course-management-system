import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';

import { FullTracePage } from './FullTracePage';

describe('FullTracePage', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('loads patterns for the filter dropdown and renders backend trace evidence', async () => {
    vi.stubGlobal('fetch', createTraceFetchMock());

    render(<FullTracePage />);

    expect(await screen.findByRole('heading', { name: /Design Pattern Trace/i })).toBeInTheDocument();
    expect(screen.getByText(/Only official backend PatternTraceService events/i)).toBeInTheDocument();
    expect(await screen.findByRole('option', { name: 'Adapter' })).toBeInTheDocument();
    expect(screen.getAllByText('Memento')).not.toHaveLength(0);
    expect(screen.getByText('Edit instructor feedback')).toBeInTheDocument();
    expect(screen.getByText('BEHAVIORAL')).toBeInTheDocument();
    expect(screen.getByText('FeedbackDraft')).toBeInTheDocument();
    expect(screen.getByText('Instructor review')).toBeInTheDocument();
    expect(screen.getByText('Instructor restored feedback draft')).toBeInTheDocument();
  });

  it('applies trace filters as backend query parameters', async () => {
    const fetchMock = createTraceFetchMock();
    vi.stubGlobal('fetch', fetchMock);
    const user = userEvent.setup();

    render(<FullTracePage />);

    await screen.findByRole('option', { name: 'Adapter' });
    await user.selectOptions(screen.getByLabelText('Category'), 'STRUCTURAL');
    await user.selectOptions(screen.getByLabelText('Pattern'), 'ADAPTER');
    await user.type(screen.getByLabelText('Workflow step'), 'Analyze');
    await user.type(screen.getByLabelText('Search'), 'sandbox');
    await user.click(screen.getByRole('button', { name: /Apply Filters/i }));

    expect(await screen.findAllByText('Adapter')).not.toHaveLength(0);
    expect(fetchMock).toHaveBeenCalledWith(
      '/api/app/trace?category=STRUCTURAL&pattern=ADAPTER&workflowStep=Analyze&search=sandbox',
      expect.objectContaining({ method: 'GET' }),
    );
  });

  it('clear filters returns to unfiltered trace', async () => {
    const fetchMock = createTraceFetchMock();
    vi.stubGlobal('fetch', fetchMock);
    const user = userEvent.setup();

    render(<FullTracePage />);

    await screen.findByRole('option', { name: 'Adapter' });
    await user.selectOptions(screen.getByLabelText('Category'), 'STRUCTURAL');
    await user.click(screen.getByRole('button', { name: /Apply Filters/i }));
    await user.click(screen.getByRole('button', { name: /Clear Filters/i }));

    expect(await screen.findAllByText('Memento')).not.toHaveLength(0);
    expect(fetchMock.mock.calls.filter(([url]) => url === '/api/app/trace')).toHaveLength(2);
  });

  it('shows an honest empty state for empty backend trace results', async () => {
    vi.stubGlobal('fetch', createTraceFetchMock({ empty: true }));

    render(<FullTracePage />);

    expect(await screen.findByText(/No backend trace events match the current filters/i)).toBeInTheDocument();
  });
});

type TraceMockOptions = {
  empty?: boolean;
};

function createTraceFetchMock(options: TraceMockOptions = {}) {
  return vi.fn((url: string) => {
    if (url === '/api/app/patterns') {
      return Promise.resolve(jsonResponse([
        { key: 'ADAPTER', displayName: 'Adapter', category: 'STRUCTURAL' },
        { key: 'MEMENTO', displayName: 'Memento', category: 'BEHAVIORAL' },
      ]));
    }

    if (url.startsWith('/api/app/trace?')) {
      return Promise.resolve(jsonResponse([
        traceEvent({
          pattern: 'ADAPTER',
          patternDisplayName: 'Adapter',
          category: 'STRUCTURAL',
          className: 'MockJavaSandboxAdapter',
          description: 'Mock Java sandbox/test runner analyzed code',
          workflowStep: 'Analyze',
        }),
      ]));
    }

    if (url === '/api/app/trace') {
      return Promise.resolve(jsonResponse(options.empty ? [] : [
        traceEvent({
          pattern: 'MEMENTO',
          patternDisplayName: 'Memento',
          category: 'BEHAVIORAL',
          className: 'FeedbackDraft',
          description: 'Instructor restored feedback draft',
          workflowStep: 'Instructor review',
        }),
      ]));
    }

    return Promise.resolve(jsonResponse({}, 404));
  });
}

function traceEvent(overrides: Partial<ReturnType<typeof baseTraceEvent>>) {
  return {
    ...baseTraceEvent(),
    ...overrides,
  };
}

function baseTraceEvent() {
  return {
    timestamp: '2026-05-28T21:00:00Z',
    userAction: 'Edit instructor feedback',
    pattern: 'MEMENTO',
    patternDisplayName: 'Memento',
    category: 'BEHAVIORAL',
    className: 'FeedbackDraft',
    description: 'Instructor restored feedback draft',
    workflowStep: 'Instructor review',
  };
}

function jsonResponse(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}
