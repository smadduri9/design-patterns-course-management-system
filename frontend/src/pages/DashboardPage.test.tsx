import { render, screen, within } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { afterEach, describe, expect, it, vi } from 'vitest';

import { DashboardPage } from './DashboardPage';

describe('DashboardPage', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('renders mocked instructor and count data from the dashboard API', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn((url: string) => {
        if (url === '/api/app/dashboard') {
          return Promise.resolve(jsonResponse({
            instructor: { id: '100', name: 'Sriram Madduri', role: 'INSTRUCTOR' },
            counts: { courses: 2, students: 5, assignments: 3, submissions: 4, traceEvents: 7 },
          }));
        }
        if (url === '/api/app/patterns') {
          return Promise.resolve(jsonResponse([
            { key: 'ADAPTER', displayName: 'Adapter', category: 'STRUCTURAL' },
            { key: 'MEMENTO', displayName: 'Memento', category: 'BEHAVIORAL' },
          ]));
        }
        if (url === '/api/app/trace') {
          return Promise.resolve(jsonResponse([]));
        }
        return Promise.resolve(jsonResponse({}, 404));
      }),
    );

    render(
      <MemoryRouter>
        <DashboardPage />
      </MemoryRouter>,
    );

    expect(await screen.findByRole('heading', { name: 'Sriram Madduri' })).toBeInTheDocument();
    expect(countCard('Courses')).toHaveTextContent('2');
    expect(countCard('Students')).toHaveTextContent('5');
    expect(countCard('Assignments')).toHaveTextContent('3');
    expect(countCard('Submissions')).toHaveTextContent('4');
    expect(countCard('Trace Events')).toHaveTextContent('7');
    expect(screen.getByText(/Adapter · STRUCTURAL/i)).toBeInTheDocument();
    expect(screen.getByText(/Memento · BEHAVIORAL/i)).toBeInTheDocument();
  });
});

function countCard(label: string) {
  return within(screen.getByLabelText('Dashboard counts')).getByText(label).closest('article')!;
}

function jsonResponse(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}
