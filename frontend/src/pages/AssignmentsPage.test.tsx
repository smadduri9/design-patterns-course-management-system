import { render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { afterEach, describe, expect, it, vi } from 'vitest';

import { AssignmentsPage } from './AssignmentsPage';

const instructor = { id: 'instructor-1', name: 'Sriram Madduri', role: 'INSTRUCTOR' };

describe('AssignmentsPage', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('shows a loading state while backend assignments are loading', async () => {
    vi.stubGlobal('fetch', vi.fn(() => new Promise(() => undefined)));

    renderAssignmentsPage();

    expect(screen.getByText(/Loading assignments/i)).toBeInTheDocument();
  });

  it('loads assignments when a course is selected', async () => {
    const fetchMock = createAssignmentsFetchMock({
      courses: [
        course('course-1', 'First Course', 0, 0),
        course('course-2', 'Second Course', 0, 1),
      ],
      assignments: {
        'course-1': [],
        'course-2': [assignment('assignment-2', 'Composite Lab', 'course-2')],
      },
    });
    vi.stubGlobal('fetch', fetchMock);
    const user = userEvent.setup();

    renderAssignmentsPage();

    await screen.findByText(/No assignments from the backend/i);
    await user.selectOptions(screen.getAllByLabelText('Course')[0], 'course-2');

    expect(await screen.findByText('Composite Lab')).toBeInTheDocument();
    expect(fetchMock).toHaveBeenCalledWith('/api/app/courses/course-2/assignments', expect.objectContaining({
      method: 'GET',
    }));
  });

  it('shows an empty assignment state for a selected course', async () => {
    vi.stubGlobal('fetch', createAssignmentsFetchMock({
      courses: [course('course-1', 'Empty Course', 0, 0)],
      assignments: { 'course-1': [] },
    }));

    renderAssignmentsPage();

    expect(await screen.findByText(/No assignments from the backend for this course yet/i)).toBeInTheDocument();
  });

  it('renders loaded assignment details from the backend', async () => {
    vi.stubGlobal('fetch', createAssignmentsFetchMock({
      courses: [course('course-1', 'Design Patterns CS501', 0, 1)],
      assignments: { 'course-1': [assignment('assignment-1', 'Adapter Essay', 'course-1')] },
    }));

    renderAssignmentsPage();

    expect(await screen.findByText('Adapter Essay')).toBeInTheDocument();
    expect(screen.getByText('Explain the Adapter pattern.')).toBeInTheDocument();
    expect(screen.getByText('PDF_TEXT')).toBeInTheDocument();
    expect(screen.getByText('Adapter Rubric · 1 criteria')).toBeInTheDocument();
    expect(screen.getByText('assignment-1')).toBeInTheDocument();
  });

  it('creates an assignment and refreshes the assignment list', async () => {
    const fetchMock = createAssignmentsFetchMock({
      courses: [course('course-1', 'Design Patterns CS501', 0, 0)],
      assignments: { 'course-1': [] },
      assignmentsAfterCreate: {
        'course-1': [assignment('created-assignment', 'Strategy Project', 'course-1')],
      },
      coursesAfterCreate: [course('course-1', 'Design Patterns CS501', 0, 1)],
    });
    vi.stubGlobal('fetch', fetchMock);
    const user = userEvent.setup();

    renderAssignmentsPage();

    await screen.findByText(/No assignments from the backend/i);
    await user.type(screen.getByLabelText(/Assignment title/i), 'Strategy Project');
    await user.type(screen.getAllByLabelText(/^Description$/i)[0], 'Choose and compare grading strategies.');
    await user.clear(screen.getByLabelText(/^Due date$/i));
    await user.type(screen.getByLabelText(/^Due date$/i), '2026-06-15');
    await user.selectOptions(screen.getByLabelText(/Accepted submission type/i), 'JAVA_CODE');
    await user.selectOptions(screen.getByLabelText(/Grading strategy/i), 'CODE_TEST');
    await user.clear(screen.getAllByLabelText(/^Max points$/i)[0]);
    await user.type(screen.getAllByLabelText(/^Max points$/i)[0], '50');
    await user.clear(screen.getByLabelText(/Rubric title/i));
    await user.type(screen.getByLabelText(/Rubric title/i), 'Strategy Rubric');
    const criterion = screen.getByRole('group', { name: /Criterion 1/i });
    await user.type(within(criterion).getByLabelText(/^Name$/i), 'Correctness');
    await user.type(within(criterion).getByLabelText(/^Description$/i), 'Code compiles and uses Strategy.');
    await user.clear(within(criterion).getByLabelText(/^Max points$/i));
    await user.type(within(criterion).getByLabelText(/^Max points$/i), '50');
    await user.click(screen.getByRole('button', { name: /Create assignment/i }));

    expect(await screen.findByText('Strategy Project')).toBeInTheDocument();
    const postCall = fetchMock.mock.calls.find(([url, init]) =>
      url === '/api/app/courses/course-1/assignments' && init?.method === 'POST',
    );
    expect(postCall).toBeDefined();
    expect(JSON.parse(postCall?.[1]?.body as string)).toEqual({
      title: 'Strategy Project',
      description: 'Choose and compare grading strategies.',
      dueDate: '2026-06-15',
      acceptedSubmissionTypes: ['JAVA_CODE'],
      gradingStrategyType: 'CODE_TEST',
      maxPoints: 50,
      rubric: {
        title: 'Strategy Rubric',
        criteria: [{ name: 'Correctness', description: 'Code compiles and uses Strategy.', maxPoints: 50 }],
      },
    });
    await waitFor(() => {
      expect(fetchMock).toHaveBeenCalledWith('/api/app/courses/course-1/assignments', expect.objectContaining({
        method: 'GET',
      }));
    });
  });
});

function renderAssignmentsPage() {
  return render(
    <MemoryRouter>
      <AssignmentsPage />
    </MemoryRouter>,
  );
}

type CourseFixture = ReturnType<typeof course>;
type AssignmentFixture = ReturnType<typeof assignment>;

type FetchMockOptions = {
  courses: CourseFixture[];
  coursesAfterCreate?: CourseFixture[];
  assignments: Record<string, AssignmentFixture[]>;
  assignmentsAfterCreate?: Record<string, AssignmentFixture[]>;
  stallAssignments?: boolean;
};

function createAssignmentsFetchMock(options: FetchMockOptions) {
  let courses = options.courses;
  let assignments = options.assignments;

  return vi.fn((url: string, init?: RequestInit) => {
    if (url === '/api/app/courses' && (!init || init.method === 'GET')) {
      return Promise.resolve(jsonResponse(courses));
    }

    const assignmentListMatch = url.match(/^\/api\/app\/courses\/([^/]+)\/assignments$/);
    if (assignmentListMatch && (!init || init.method === 'GET')) {
      if (options.stallAssignments) {
        return new Promise(() => undefined);
      }
      return Promise.resolve(jsonResponse(assignments[assignmentListMatch[1]] ?? []));
    }

    if (assignmentListMatch && init?.method === 'POST') {
      if (options.assignmentsAfterCreate) {
        assignments = options.assignmentsAfterCreate;
      }
      if (options.coursesAfterCreate) {
        courses = options.coursesAfterCreate;
      }
      const courseId = assignmentListMatch[1];
      return Promise.resolve(jsonResponse(assignments[courseId]?.at(-1) ?? assignment('created-assignment', 'Created Assignment', courseId)));
    }

    return Promise.resolve(jsonResponse({}, 404));
  });
}

function course(id: string, title: string, rosterCount: number, assignmentCount: number) {
  return {
    id,
    title,
    instructor,
    rosterCount,
    assignmentCount,
  };
}

function assignment(id: string, title: string, courseId: string) {
  return {
    id,
    courseId,
    title,
    description: title === 'Adapter Essay' ? 'Explain the Adapter pattern.' : 'Choose and compare grading strategies.',
    dueDate: '2026-06-15',
    acceptedSubmissionTypes: title === 'Strategy Project' ? ['JAVA_CODE'] : ['PDF_TEXT'],
    gradingStrategyType: 'RUBRIC_WEIGHTED',
    maxPoints: title === 'Strategy Project' ? 50 : 100,
    rubric: {
      id: `${id}-rubric`,
      title: title === 'Strategy Project' ? 'Strategy Rubric' : 'Adapter Rubric',
      criteria: [
        {
          id: `${id}-criterion`,
          name: 'Correctness',
          description: 'Uses the pattern correctly.',
          maxPoints: title === 'Strategy Project' ? 50 : 100,
        },
      ],
    },
  };
}

function jsonResponse(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}
