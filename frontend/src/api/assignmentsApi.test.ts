import { afterEach, describe, expect, it, vi } from 'vitest';

import { createAssignment, getAssignment, getCourseAssignments } from './assignmentsApi';
import type { CreateAssignmentRequest } from './types';

describe('assignmentsApi', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('calls assignment list and detail endpoints', async () => {
    const fetchMock = vi.fn().mockImplementation(() => Promise.resolve(jsonResponse([])));
    vi.stubGlobal('fetch', fetchMock);

    await getCourseAssignments('course-1');
    await getAssignment('assignment-1');

    expect(fetchMock).toHaveBeenNthCalledWith(1, '/api/app/courses/course-1/assignments', {
      method: 'GET',
      headers: { Accept: 'application/json' },
    });
    expect(fetchMock).toHaveBeenNthCalledWith(2, '/api/app/assignments/assignment-1', {
      method: 'GET',
      headers: { Accept: 'application/json' },
    });
  });

  it('posts assignment creation payload to the course assignment endpoint', async () => {
    const fetchMock = vi.fn().mockResolvedValue(jsonResponse({}));
    vi.stubGlobal('fetch', fetchMock);
    const request: CreateAssignmentRequest = {
      title: 'Adapter Assignment',
      description: 'Explain Adapter.',
      dueDate: '2026-06-01',
      acceptedSubmissionTypes: ['PDF_TEXT'],
      gradingStrategyType: 'RUBRIC_WEIGHTED',
      maxPoints: 100,
      rubric: {
        title: 'Adapter Rubric',
        criteria: [{ name: 'Correctness', description: 'Uses Adapter correctly.', maxPoints: 100 }],
      },
    };

    await createAssignment('course-1', request);

    expect(fetchMock).toHaveBeenCalledWith('/api/app/courses/course-1/assignments', {
      method: 'POST',
      headers: { Accept: 'application/json', 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
    });
  });
});

function jsonResponse(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}
