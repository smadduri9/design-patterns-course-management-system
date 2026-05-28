import { render, screen } from '@testing-library/react';
import { describe, expect, it, vi, afterEach } from 'vitest';

import { FullTracePage } from './FullTracePage';

describe('FullTracePage', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('renders mocked trace events from the backend API', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue(jsonResponse([
      {
        timestamp: '2026-05-28T21:00:00Z',
        userAction: 'Edit instructor feedback',
        pattern: 'MEMENTO',
        patternDisplayName: 'Memento',
        category: 'BEHAVIORAL',
        className: 'FeedbackDraft',
        description: 'Instructor restored feedback draft',
        workflowStep: 'Instructor review',
      },
    ])));

    render(<FullTracePage />);

    expect(await screen.findByRole('heading', { name: /Backend Pattern Trace Events/i })).toBeInTheDocument();
    expect(screen.getByText('Memento')).toBeInTheDocument();
    expect(screen.getByText('FeedbackDraft')).toBeInTheDocument();
    expect(screen.getByText('Instructor restored feedback draft')).toBeInTheDocument();
  });
});

function jsonResponse(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}
