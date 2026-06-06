import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';

import { StudentFeedbackPage } from './StudentFeedbackPage';

const instructor = { id: 'instructor-1', name: 'Sriram Madduri', role: 'INSTRUCTOR' };
const student = { id: 'student-1', name: 'Demo Student 1', role: 'STUDENT' };

describe('StudentFeedbackPage', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('fetches and renders finalized student feedback', async () => {
    const fetchMock = createStudentFeedbackFetchMock({ finalized: true });
    vi.stubGlobal('fetch', fetchMock);

    render(<StudentFeedbackPage />);

    expect(await screen.findByRole('heading', { name: /Final feedback/i })).toBeInTheDocument();
    expect(screen.getByText('Final feedback for the student.')).toBeInTheDocument();
    expect(screen.getByText(/95\/100/i)).toBeInTheDocument();
    expect(screen.getByText('Mock AI summary visible to the student.')).toBeInTheDocument();
    expect(screen.getByText('Feedback is ready.')).toBeInTheDocument();
    expect(fetchMock).toHaveBeenCalledWith('/api/app/submissions/submission-1/student-feedback', expect.objectContaining({
      method: 'GET',
    }));
  });

  it('handles not-finalized student feedback state honestly', async () => {
    vi.stubGlobal('fetch', createStudentFeedbackFetchMock({ finalized: false }));

    render(<StudentFeedbackPage />);

    expect(await screen.findByText(/Student feedback is not finalized or unavailable/i)).toBeInTheDocument();
    expect(screen.queryByText('Final feedback for the student.')).not.toBeInTheDocument();
  });

  it('loads feedback for a selected submission', async () => {
    const fetchMock = createStudentFeedbackFetchMock({ finalized: true, includeSecondSubmission: true });
    vi.stubGlobal('fetch', fetchMock);
    const user = userEvent.setup();

    render(<StudentFeedbackPage />);

    await screen.findByText('Final feedback for the student.');
    await user.click(screen.getByRole('button', { name: /Demo Student 2/i }));

    expect(await screen.findByText('Final feedback for Demo Student 2.')).toBeInTheDocument();
    expect(fetchMock).toHaveBeenCalledWith('/api/app/submissions/submission-2/student-feedback', expect.objectContaining({
      method: 'GET',
    }));
  });
});

type StudentFeedbackFetchOptions = {
  finalized: boolean;
  includeSecondSubmission?: boolean;
};

function createStudentFeedbackFetchMock(options: StudentFeedbackFetchOptions) {
  return vi.fn((url: string) => {
    if (url === '/api/app/courses') {
      return Promise.resolve(jsonResponse([course()]));
    }
    if (url === '/api/app/courses/course-1/assignments') {
      return Promise.resolve(jsonResponse([assignment()]));
    }
    if (url === '/api/app/assignments/assignment-1/submissions') {
      return Promise.resolve(jsonResponse([
        submission('submission-1', student),
        ...(options.includeSecondSubmission ? [submission('submission-2', { id: 'student-2', name: 'Demo Student 2', role: 'STUDENT' })] : []),
      ]));
    }
    if (url === '/api/app/submissions/submission-1/student-feedback') {
      return options.finalized
        ? Promise.resolve(jsonResponse(studentFeedback('submission-1', 'Final feedback for the student.')))
        : Promise.resolve(jsonResponse({ message: 'Feedback is not finalized' }, 409));
    }
    if (url === '/api/app/submissions/submission-2/student-feedback') {
      return Promise.resolve(jsonResponse(studentFeedback('submission-2', 'Final feedback for Demo Student 2.')));
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

function submission(id: string, submissionStudent: typeof student) {
  return {
    id,
    assignmentId: 'assignment-1',
    student: submissionStudent,
    type: 'PDF_TEXT',
    status: 'FINALIZED',
    submittedAt: '2026-05-28T21:00:00Z',
    hasAnalysisReport: true,
  };
}

function studentFeedback(submissionId: string, finalFeedback: string) {
  return {
    submissionId,
    finalFeedback,
    grade: { points: 95, maxPoints: 100, explanation: 'Excellent work.' },
    aiSummary: 'Mock AI summary visible to the student.',
    notification: {
      id: 'notification-1',
      message: 'Feedback is ready.',
      createdAt: '2026-05-28T21:30:00Z',
      read: false,
    },
    report: {
      id: 'report-1',
      summary: 'Mock AI summary visible to the student.',
      rubricFindings: [],
      testResults: [],
      suggestedFeedback: 'Suggested feedback.',
      gradeSuggestion: { points: 95, maxPoints: 100, explanation: 'Excellent work.' },
    },
  };
}

function jsonResponse(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}
