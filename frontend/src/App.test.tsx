import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { afterEach, describe, expect, it, vi } from 'vitest';

import { App } from './App';

describe('App shell', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('renders the dashboard route inside the shell', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn((url: string) => {
        if (url === '/api/app/dashboard') {
          return Promise.resolve(jsonResponse({
            instructor: { id: 'instructor-id', name: 'Sriram Madduri', role: 'INSTRUCTOR' },
            counts: { courses: 1, students: 5, assignments: 2, submissions: 3, traceEvents: 4 },
          }));
        }
        if (url === '/api/app/patterns') {
          return Promise.resolve(jsonResponse([]));
        }
        if (url === '/api/app/trace') {
          return Promise.resolve(jsonResponse([]));
        }
        return Promise.resolve(jsonResponse({}, 404));
      }),
    );

    render(
      <MemoryRouter initialEntries={['/']}>
        <App />
      </MemoryRouter>,
    );

    expect(screen.getByRole('heading', { name: /course management system/i })).toBeInTheDocument();
    expect(await screen.findByRole('heading', { name: /sriram madduri/i })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /dashboard/i })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /^courses$/i })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /course builder/i })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /students/i })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /assignments/i })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /submissions/i })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /student feedback/i })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: /full trace/i })).toBeInTheDocument();
  });

  it('renders pending pages for future workflow routes without business placeholders', () => {
    vi.stubGlobal('fetch', vi.fn());

    render(
      <MemoryRouter initialEntries={['/courses']}>
        <App />
      </MemoryRouter>,
    );

    expect(screen.getByRole('heading', { name: /^courses$/i })).toBeInTheDocument();
    expect(screen.getAllByText(/Backend API pending/i)).not.toHaveLength(0);
    expect(screen.getByText(/No mock records shown/i)).toBeInTheDocument();
  });
});

function jsonResponse(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}
