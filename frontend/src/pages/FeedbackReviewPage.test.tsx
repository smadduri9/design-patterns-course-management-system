import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { afterEach, describe, expect, it, vi } from 'vitest';

import { FeedbackReviewPage } from './FeedbackReviewPage';

const instructor = { id: 'instructor-1', name: 'Sriram Madduri', role: 'INSTRUCTOR' };
const student = { id: 'student-1', name: 'Demo Student 1', role: 'STUDENT' };

describe('FeedbackReviewPage', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('loads submissions through the feedback review selection flow', async () => {
    const fetchMock = createFeedbackFetchMock();
    vi.stubGlobal('fetch', fetchMock);

    renderFeedbackReviewPage();

    expect(await screen.findByRole('button', { name: /Demo Student 1/i })).toBeInTheDocument();
    expect(fetchMock).toHaveBeenCalledWith('/api/app/courses/course-1/assignments', expect.objectContaining({
      method: 'GET',
    }));
    expect(fetchMock).toHaveBeenCalledWith('/api/app/assignments/assignment-1/submissions', expect.objectContaining({
      method: 'GET',
    }));
  });

  it('saves and restores feedback drafts with backend payloads', async () => {
    const fetchMock = createFeedbackFetchMock();
    vi.stubGlobal('fetch', fetchMock);
    const user = userEvent.setup();

    renderFeedbackReviewPage();

    await user.click(await screen.findByRole('button', { name: /Demo Student 1/i }));
    await user.clear(await screen.findByLabelText(/Current feedback/i));
    await user.type(screen.getByLabelText(/Current feedback/i), 'Updated draft feedback');
    await user.click(screen.getByRole('button', { name: /Save Draft/i }));

    expect(fetchMock).toHaveBeenCalledWith('/api/app/submissions/submission-1/feedback-drafts', expect.objectContaining({
      method: 'POST',
      body: JSON.stringify({ feedbackText: 'Updated draft feedback' }),
    }));
    expect(await screen.findByDisplayValue('Saved draft feedback')).toBeInTheDocument();

    const snapshots = screen.getByLabelText('Feedback draft snapshots');
    await user.click(within(snapshots).getAllByRole('button', { name: /Restore/i })[0]);

    expect(fetchMock).toHaveBeenCalledWith('/api/app/submissions/submission-1/feedback-drafts/restore', expect.objectContaining({
      method: 'POST',
      body: JSON.stringify({ draftIndex: 0 }),
    }));
    expect(await screen.findByDisplayValue('Restored draft feedback')).toBeInTheDocument();
  });

  it('sends final feedback and renders the finalized result', async () => {
    const fetchMock = createFeedbackFetchMock();
    vi.stubGlobal('fetch', fetchMock);
    const user = userEvent.setup();

    renderFeedbackReviewPage();

    await user.click(await screen.findByRole('button', { name: /Demo Student 1/i }));
    await user.clear(await screen.findByLabelText(/Current feedback/i));
    await user.type(screen.getByLabelText(/Current feedback/i), 'Final instructor feedback');
    await user.click(screen.getByRole('button', { name: /Send Final Feedback/i }));

    expect(fetchMock).toHaveBeenCalledWith('/api/app/submissions/submission-1/final-feedback', expect.objectContaining({
      method: 'POST',
      body: JSON.stringify({ feedbackText: 'Final instructor feedback' }),
    }));
    expect(await screen.findByRole('heading', { name: /Final feedback sent/i })).toBeInTheDocument();
    expect(screen.getAllByText('Final instructor feedback')).not.toHaveLength(0);
    expect(screen.getAllByText((_, element) => element?.textContent?.includes('92/100') ?? false)).not.toHaveLength(0);
  });
});

function renderFeedbackReviewPage() {
  return render(
    <MemoryRouter>
      <FeedbackReviewPage />
    </MemoryRouter>,
  );
}

function createFeedbackFetchMock() {
  let drafts = draftResponse('Initial draft feedback', ['Initial draft feedback']);
  let finalized = false;

  return vi.fn((url: string, init?: RequestInit) => {
    if (url === '/api/app/courses') {
      return Promise.resolve(jsonResponse([course()]));
    }
    if (url === '/api/app/courses/course-1/assignments') {
      return Promise.resolve(jsonResponse([assignment()]));
    }
    if (url === '/api/app/assignments/assignment-1/submissions') {
      return Promise.resolve(jsonResponse([submission(finalized)]));
    }
    if (url === '/api/app/submissions/submission-1') {
      return Promise.resolve(jsonResponse(submissionDetail(finalized)));
    }
    if (url === '/api/app/submissions/submission-1/feedback-drafts' && (!init || init.method === 'GET')) {
      return Promise.resolve(jsonResponse(drafts));
    }
    if (url === '/api/app/submissions/submission-1/feedback-drafts' && init?.method === 'POST') {
      drafts = draftResponse('Saved draft feedback', ['Initial draft feedback', 'Saved draft feedback']);
      return Promise.resolve(jsonResponse(drafts));
    }
    if (url === '/api/app/submissions/submission-1/feedback-drafts/restore' && init?.method === 'POST') {
      drafts = draftResponse('Restored draft feedback', ['Restored draft feedback']);
      return Promise.resolve(jsonResponse(drafts));
    }
    if (url === '/api/app/submissions/submission-1/final-feedback' && init?.method === 'POST') {
      finalized = true;
      return Promise.resolve(jsonResponse(finalFeedback()));
    }
    if (url === '/api/app/trace') {
      return Promise.resolve(jsonResponse([]));
    }
    return Promise.resolve(jsonResponse({}, 404));
  });
}

function course() {
  return {
    id: 'course-1',
    title: 'Design Patterns CS501',
    instructor,
    rosterCount: 1,
    assignmentCount: 1,
  };
}

function assignment() {
  return {
    id: 'assignment-1',
    courseId: 'course-1',
    title: 'Adapter Essay',
    description: 'Explain Adapter.',
    dueDate: '2026-06-15',
    acceptedSubmissionTypes: ['PDF_TEXT'],
    gradingStrategyType: 'RUBRIC_WEIGHTED',
    maxPoints: 100,
    rubric: { id: 'rubric-1', title: 'Rubric', criteria: [] },
  };
}

function submission(finalized = false) {
  return {
    id: 'submission-1',
    assignmentId: 'assignment-1',
    student,
    type: 'PDF_TEXT',
    status: finalized ? 'FINALIZED' : 'AWAITING_REVIEW',
    submittedAt: '2026-05-28T21:00:00Z',
    hasAnalysisReport: true,
  };
}

function submissionDetail(finalized = false) {
  return {
    ...submission(finalized),
    report: {
      id: 'report-1',
      summary: 'Mock AI summary for feedback.',
      rubricFindings: [],
      testResults: [],
      suggestedFeedback: 'Suggested feedback from Mock AI.',
      gradeSuggestion: { points: 92, maxPoints: 100, explanation: 'Strong work.' },
    },
  };
}

function draftResponse(currentFeedback: string, feedbackTexts: string[]) {
  return {
    submissionId: 'submission-1',
    currentFeedback,
    drafts: feedbackTexts.map((feedbackText, index) => ({
      index,
      feedbackText,
      savedAt: '2026-05-28T21:00:00Z',
    })),
  };
}

function finalFeedback() {
  return {
    submissionId: 'submission-1',
    status: 'FINALIZED',
    finalFeedback: 'Final instructor feedback',
    grade: { points: 92, maxPoints: 100, explanation: 'Strong work.' },
    notification: {
      id: 'notification-1',
      message: 'Feedback is ready.',
      createdAt: '2026-05-28T21:30:00Z',
      read: false,
    },
  };
}

function jsonResponse(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}
