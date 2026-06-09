import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { afterEach, describe, expect, it, vi } from 'vitest';

import { SubmissionsPage } from './SubmissionsPage';

const instructor = { id: 'instructor-1', name: 'Sriram Madduri', role: 'INSTRUCTOR' };
const studentOne = { id: 'student-1', name: 'Demo Student 1', role: 'STUDENT' };
const studentTwo = { id: 'student-2', name: 'Demo Student 2', role: 'STUDENT' };

describe('SubmissionsPage', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('shows a loading state while backend data is loading', () => {
    vi.stubGlobal('fetch', vi.fn(() => new Promise(() => undefined)));

    renderSubmissionsPage();

    expect(screen.getByText(/Loading submissions/i)).toBeInTheDocument();
  });

  it('selecting a course loads assignments', async () => {
    const fetchMock = createSubmissionsFetchMock({
      courses: [
        course('course-1', 'First Course'),
        course('course-2', 'Second Course'),
      ],
      assignments: {
        'course-1': [],
        'course-2': [assignment('assignment-2', 'Adapter Essay', 'course-2')],
      },
      submissions: { 'assignment-2': [] },
    });
    vi.stubGlobal('fetch', fetchMock);
    const user = userEvent.setup();

    renderSubmissionsPage();

    await screen.findByText(/This course has no backend assignments yet/i);
    await user.selectOptions(screen.getByLabelText('Course'), 'course-2');

    expect(await screen.findByText(/No submissions from the backend/i)).toBeInTheDocument();
    expect(fetchMock).toHaveBeenCalledWith('/api/app/courses/course-2/assignments', expect.objectContaining({
      method: 'GET',
    }));
  });

  it('selecting an assignment loads submissions', async () => {
    const fetchMock = createSubmissionsFetchMock({
      courses: [course('course-1', 'Design Patterns CS501')],
      assignments: {
        'course-1': [
          assignment('assignment-1', 'Adapter Essay', 'course-1'),
          assignment('assignment-2', 'Strategy Project', 'course-1'),
        ],
      },
      submissions: {
        'assignment-1': [],
        'assignment-2': [submission('submission-2', 'assignment-2', studentTwo, false)],
      },
    });
    vi.stubGlobal('fetch', fetchMock);
    const user = userEvent.setup();

    renderSubmissionsPage();

    await screen.findByText(/No submissions from the backend/i);
    await user.selectOptions(screen.getByLabelText('Assignment'), 'assignment-2');

    expect(await screen.findByRole('button', { name: /Demo Student 2/i })).toBeInTheDocument();
    expect(fetchMock).toHaveBeenCalledWith('/api/app/assignments/assignment-2/submissions', expect.objectContaining({
      method: 'GET',
    }));
  });

  it('shows an empty submissions state for a selected assignment', async () => {
    vi.stubGlobal('fetch', createSubmissionsFetchMock({
      courses: [course('course-1', 'Design Patterns CS501')],
      assignments: { 'course-1': [assignment('assignment-1', 'Adapter Essay', 'course-1')] },
      submissions: { 'assignment-1': [] },
    }));

    renderSubmissionsPage();

    expect(await screen.findByText(/No submissions from the backend for this assignment yet/i)).toBeInTheDocument();
  });

  it('creates a submission and refreshes the submission list', async () => {
    const fetchMock = createSubmissionsFetchMock({
      courses: [course('course-1', 'Design Patterns CS501')],
      assignments: { 'course-1': [assignment('assignment-1', 'Adapter Essay', 'course-1')] },
      submissions: { 'assignment-1': [] },
      submissionsAfterCreate: {
        'assignment-1': [submission('created-submission', 'assignment-1', studentTwo, false)],
      },
    });
    vi.stubGlobal('fetch', fetchMock);
    const user = userEvent.setup();

    renderSubmissionsPage();

    await screen.findByText(/No submissions from the backend/i);
    await user.selectOptions(screen.getByLabelText('Student'), 'student-2');
    await user.selectOptions(screen.getByLabelText('Submission type'), 'JAVA_CODE');
    await user.type(screen.getByLabelText('Content'), 'class Solution');
    await user.click(screen.getByRole('button', { name: /Create submission/i }));

    expect(await screen.findByRole('button', { name: /Demo Student 2/i })).toBeInTheDocument();
    const postCall = fetchMock.mock.calls.find(([url, init]) =>
      url === '/api/app/assignments/assignment-1/submissions' && init?.method === 'POST',
    );
    expect(postCall).toBeDefined();
    expect(JSON.parse(postCall?.[1]?.body as string)).toEqual({
      studentId: 'student-2',
      submissionType: 'JAVA_CODE',
      content: 'class Solution',
    });
    expect(screen.getByText(/Creating a submission does not automatically run analysis/i)).toBeInTheDocument();
  });

  it('selecting a submission loads detail', async () => {
    vi.stubGlobal('fetch', createSubmissionsFetchMock({
      courses: [course('course-1', 'Design Patterns CS501')],
      assignments: { 'course-1': [assignment('assignment-1', 'Adapter Essay', 'course-1')] },
      submissions: { 'assignment-1': [submission('submission-1', 'assignment-1', studentOne, false)] },
    }));
    const user = userEvent.setup();

    renderSubmissionsPage();

    await user.click(await screen.findByRole('button', { name: /Demo Student 1/i }));

    await waitFor(() => {
      expect(screen.getAllByText('Adapter Essay')).toHaveLength(2);
    });
    expect(screen.getByText('SUBMITTED')).toBeInTheDocument();
    expect(screen.getByText(/Not returned by the current backend detail API/i)).toBeInTheDocument();
  });

  it('runs Mock AI Analysis and renders the report from the API response', async () => {
    const fetchMock = createSubmissionsFetchMock({
      courses: [course('course-1', 'Design Patterns CS501')],
      assignments: { 'course-1': [assignment('assignment-1', 'Adapter Essay', 'course-1')] },
      submissions: { 'assignment-1': [submission('submission-1', 'assignment-1', studentOne, false)] },
    });
    vi.stubGlobal('fetch', fetchMock);
    const user = userEvent.setup();

    renderSubmissionsPage();

    await user.click(await screen.findByRole('button', { name: /Demo Student 1/i }));
    await user.click(await screen.findByRole('button', { name: /Run Mock AI Analysis/i }));

    expect(fetchMock).toHaveBeenCalledWith('/api/app/submissions/submission-1/analyze', expect.objectContaining({
      method: 'POST',
    }));
    expect(await screen.findByRole('heading', { name: /Mock AI Analysis Report/i })).toBeInTheDocument();
    expect(screen.getByText('Mock AI found the submission meets the rubric.')).toBeInTheDocument();
    expect(screen.getByText('Uses Adapter correctly.')).toBeInTheDocument();
    expect(screen.getByText(/AdapterTest/i)).toBeInTheDocument();
    expect(screen.getByText(/90\/100/i)).toBeInTheDocument();
  });
});

type CourseFixture = ReturnType<typeof course>;
type AssignmentFixture = ReturnType<typeof assignment>;
type SubmissionFixture = ReturnType<typeof submission>;

type FetchMockOptions = {
  courses: CourseFixture[];
  assignments: Record<string, AssignmentFixture[]>;
  submissions: Record<string, SubmissionFixture[]>;
  submissionsAfterCreate?: Record<string, SubmissionFixture[]>;
};

function renderSubmissionsPage() {
  return render(
    <MemoryRouter>
      <SubmissionsPage />
    </MemoryRouter>,
  );
}

function createSubmissionsFetchMock(options: FetchMockOptions) {
  let submissions = options.submissions;
  const analyzedSubmissionIds = new Set<string>();

  return vi.fn((url: string, init?: RequestInit) => {
    if (url === '/api/app/courses' && (!init || init.method === 'GET')) {
      return Promise.resolve(jsonResponse(options.courses));
    }

    if (url === '/api/app/students') {
      return Promise.resolve(jsonResponse([studentOne, studentTwo]));
    }

    if (url === '/api/app/trace') {
      return Promise.resolve(jsonResponse([]));
    }

    const assignmentListMatch = url.match(/^\/api\/app\/courses\/([^/]+)\/assignments$/);
    if (assignmentListMatch) {
      return Promise.resolve(jsonResponse(options.assignments[assignmentListMatch[1]] ?? []));
    }

    const submissionListMatch = url.match(/^\/api\/app\/assignments\/([^/]+)\/submissions$/);
    if (submissionListMatch && (!init || init.method === 'GET')) {
      return Promise.resolve(jsonResponse(submissions[submissionListMatch[1]] ?? []));
    }

    if (submissionListMatch && init?.method === 'POST') {
      if (options.submissionsAfterCreate) {
        submissions = options.submissionsAfterCreate;
      }
      const assignmentId = submissionListMatch[1];
      return Promise.resolve(jsonResponse(detail(submissions[assignmentId]?.at(-1) ?? submission('created-submission', assignmentId, studentOne, false))));
    }

    const detailMatch = url.match(/^\/api\/app\/submissions\/([^/]+)$/);
    if (detailMatch) {
      return Promise.resolve(jsonResponse(detail(findSubmission(submissions, detailMatch[1]), analyzedSubmissionIds.has(detailMatch[1]))));
    }

    const analyzeMatch = url.match(/^\/api\/app\/submissions\/([^/]+)\/analyze$/);
    if (analyzeMatch && init?.method === 'POST') {
      analyzedSubmissionIds.add(analyzeMatch[1]);
      return Promise.resolve(jsonResponse({
        submissionId: analyzeMatch[1],
        status: 'AWAITING_REVIEW',
        report: report(),
      }));
    }

    return Promise.resolve(jsonResponse({}, 404));
  });
}

function findSubmission(submissions: Record<string, SubmissionFixture[]>, submissionId: string) {
  return Object.values(submissions).flat().find((item) => item.id === submissionId) ?? submission(submissionId, 'assignment-1', studentOne, false);
}

function course(id: string, title: string) {
  return {
    id,
    title,
    instructor,
    rosterCount: 0,
    assignmentCount: 1,
  };
}

function assignment(id: string, title: string, courseId: string) {
  return {
    id,
    courseId,
    title,
    description: 'Explain the Adapter pattern.',
    dueDate: '2026-06-15',
    acceptedSubmissionTypes: ['PDF_TEXT', 'JAVA_CODE'],
    gradingStrategyType: 'RUBRIC_WEIGHTED',
    maxPoints: 100,
    rubric: {
      id: `${id}-rubric`,
      title: 'Adapter Rubric',
      criteria: [{ id: `${id}-criterion`, name: 'Correctness', description: 'Uses Adapter.', maxPoints: 100 }],
    },
  };
}

function submission(id: string, assignmentId: string, student: typeof studentOne, hasAnalysisReport: boolean) {
  return {
    id,
    assignmentId,
    student,
    type: 'JAVA_CODE',
    status: hasAnalysisReport ? 'AWAITING_REVIEW' : 'SUBMITTED',
    submittedAt: '2026-05-28T21:00:00Z',
    hasAnalysisReport,
  };
}

function detail(base: SubmissionFixture, analyzed = base.hasAnalysisReport) {
  return {
    ...base,
    status: analyzed ? 'AWAITING_REVIEW' : base.status,
    hasAnalysisReport: analyzed,
    report: analyzed ? report() : null,
  };
}

function report() {
  return {
    id: 'report-1',
    summary: 'Mock AI found the submission meets the rubric.',
    rubricFindings: [{ criterionId: 'criterion-1', pointsEarned: 90, feedback: 'Uses Adapter correctly.' }],
    testResults: [{ testName: 'AdapterTest', passed: true, output: 'Mock Java sandbox/test runner passed.' }],
    suggestedFeedback: 'Strong work with a clear Adapter implementation.',
    gradeSuggestion: { points: 90, maxPoints: 100, explanation: 'Meets most rubric expectations.' },
  };
}

function jsonResponse(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}
