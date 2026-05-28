import { afterEach, describe, expect, it, vi } from 'vitest';

import {
  finalizeFeedback,
  getFeedbackDrafts,
  getStudentFeedback,
  restoreFeedbackDraft,
  saveFeedbackDraft,
} from './feedbackApi';

describe('feedbackApi', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('calls feedback read endpoints', async () => {
    const fetchMock = vi.fn().mockImplementation(() => Promise.resolve(jsonResponse({})));
    vi.stubGlobal('fetch', fetchMock);

    await getFeedbackDrafts('submission-1');
    await getStudentFeedback('submission-1');

    expect(fetchMock).toHaveBeenNthCalledWith(1, '/api/app/submissions/submission-1/feedback-drafts', {
      method: 'GET',
      headers: { Accept: 'application/json' },
    });
    expect(fetchMock).toHaveBeenNthCalledWith(2, '/api/app/submissions/submission-1/student-feedback', {
      method: 'GET',
      headers: { Accept: 'application/json' },
    });
  });

  it('posts draft, restore, and final feedback payloads', async () => {
    const fetchMock = vi.fn().mockImplementation(() => Promise.resolve(jsonResponse({})));
    vi.stubGlobal('fetch', fetchMock);

    await saveFeedbackDraft('submission-1', { feedbackText: 'Draft feedback' });
    await restoreFeedbackDraft('submission-1', { draftIndex: 2 });
    await finalizeFeedback('submission-1', { feedbackText: 'Final feedback' });

    expect(fetchMock).toHaveBeenNthCalledWith(1, '/api/app/submissions/submission-1/feedback-drafts', {
      method: 'POST',
      headers: { Accept: 'application/json', 'Content-Type': 'application/json' },
      body: JSON.stringify({ feedbackText: 'Draft feedback' }),
    });
    expect(fetchMock).toHaveBeenNthCalledWith(2, '/api/app/submissions/submission-1/feedback-drafts/restore', {
      method: 'POST',
      headers: { Accept: 'application/json', 'Content-Type': 'application/json' },
      body: JSON.stringify({ draftIndex: 2 }),
    });
    expect(fetchMock).toHaveBeenNthCalledWith(3, '/api/app/submissions/submission-1/final-feedback', {
      method: 'POST',
      headers: { Accept: 'application/json', 'Content-Type': 'application/json' },
      body: JSON.stringify({ feedbackText: 'Final feedback' }),
    });
  });
});

function jsonResponse(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}
