import { afterEach, describe, expect, it, vi } from 'vitest';

import {
  analyzeSubmission,
  createSubmission,
  getAssignmentSubmissions,
  getSubmission,
} from './submissionsApi';
import type { CreateSubmissionRequest } from './types';

describe('submissionsApi', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('calls submission list and detail endpoints', async () => {
    const fetchMock = vi.fn().mockImplementation(() => Promise.resolve(jsonResponse([])));
    vi.stubGlobal('fetch', fetchMock);

    await getAssignmentSubmissions('assignment-1');
    await getSubmission('submission-1');

    expect(fetchMock).toHaveBeenNthCalledWith(1, '/api/app/assignments/assignment-1/submissions', {
      method: 'GET',
      headers: { Accept: 'application/json' },
    });
    expect(fetchMock).toHaveBeenNthCalledWith(2, '/api/app/submissions/submission-1', {
      method: 'GET',
      headers: { Accept: 'application/json' },
    });
  });

  it('posts submission creation payload and analyze action', async () => {
    const fetchMock = vi.fn().mockImplementation(() => Promise.resolve(jsonResponse({})));
    vi.stubGlobal('fetch', fetchMock);
    const request: CreateSubmissionRequest = {
      studentId: 'student-1',
      submissionType: 'JAVA_CODE',
      content: 'class Solution {}',
    };

    await createSubmission('assignment-1', request);
    await analyzeSubmission('submission-1');

    expect(fetchMock).toHaveBeenNthCalledWith(1, '/api/app/assignments/assignment-1/submissions', {
      method: 'POST',
      headers: { Accept: 'application/json', 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
    });
    expect(fetchMock).toHaveBeenNthCalledWith(2, '/api/app/submissions/submission-1/analyze', {
      method: 'POST',
      headers: { Accept: 'application/json' },
      body: undefined,
    });
  });
});

function jsonResponse(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}
