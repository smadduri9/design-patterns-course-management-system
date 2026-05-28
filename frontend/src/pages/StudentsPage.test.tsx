import { render, screen } from '@testing-library/react';
import { describe, expect, it, vi, afterEach } from 'vitest';

import { StudentsPage } from './StudentsPage';

describe('StudentsPage', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('fetches and displays students from the backend API', async () => {
    const fetchMock = vi.fn().mockResolvedValue(jsonResponse([
      { id: 'student-1', name: 'Demo Student 1', role: 'STUDENT' },
      { id: 'student-2', name: 'Demo Student 2', role: 'STUDENT' },
      { id: 'student-3', name: 'Demo Student 3', role: 'STUDENT' },
      { id: 'student-4', name: 'Demo Student 4', role: 'STUDENT' },
      { id: 'student-5', name: 'Demo Student 5', role: 'STUDENT' },
    ]));
    vi.stubGlobal('fetch', fetchMock);

    render(<StudentsPage />);

    expect(await screen.findByText('Demo Student 1')).toBeInTheDocument();
    expect(screen.getByText('Demo Student 5')).toBeInTheDocument();
    expect(screen.getAllByText('STUDENT')).toHaveLength(5);
    expect(screen.getAllByText(/Email not provided by backend API/i)).toHaveLength(5);
    expect(fetchMock).toHaveBeenCalledWith('/api/app/students', {
      method: 'GET',
      headers: {
        Accept: 'application/json',
      },
    });
  });
});

function jsonResponse(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}
