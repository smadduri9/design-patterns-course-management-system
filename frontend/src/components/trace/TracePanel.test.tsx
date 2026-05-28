import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { afterEach, describe, expect, it, vi } from 'vitest';

import { TracePanel } from './TracePanel';

describe('TracePanel', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('renders mocked backend trace events', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(jsonResponse([
      traceEvent({
        pattern: 'ADAPTER',
        patternDisplayName: 'Adapter',
        category: 'STRUCTURAL',
        className: 'MockAIServiceAdapter',
        description: 'Adapted mock AI response for rubric analysis',
        workflowStep: 'Mock AI analysis',
      }),
    ])));

    render(
      <MemoryRouter>
        <TracePanel />
      </MemoryRouter>,
    );

    expect(await screen.findByText('Adapter')).toBeInTheDocument();
    expect(screen.getByText(/Adapted mock AI response/i)).toBeInTheDocument();
    expect(screen.getByText(/MockAIServiceAdapter/i)).toBeInTheDocument();
  });

  it('shows an empty state when the backend trace API returns no events', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(jsonResponse([])));

    render(
      <MemoryRouter>
        <TracePanel />
      </MemoryRouter>,
    );

    expect(await screen.findByText(/No backend trace events yet/i)).toBeInTheDocument();
  });
});

function traceEvent(overrides: Partial<Record<string, string>>) {
  return {
    timestamp: '2026-05-28T21:00:00Z',
    userAction: 'Run mock AI analysis',
    pattern: 'ADAPTER',
    patternDisplayName: 'Adapter',
    category: 'STRUCTURAL',
    className: 'MockAIServiceAdapter',
    description: 'Adapted mock AI response',
    workflowStep: 'Mock AI analysis',
    ...overrides,
  };
}

function jsonResponse(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}
