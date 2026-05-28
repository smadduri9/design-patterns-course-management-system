import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';

import { CoursesPage } from './CoursesPage';

const instructor = { id: 'instructor-1', name: 'Sriram Madduri', role: 'INSTRUCTOR' };
const studentOne = { id: 'student-1', name: 'Demo Student 1', role: 'STUDENT' };
const studentTwo = { id: 'student-2', name: 'Demo Student 2', role: 'STUDENT' };
const studentThree = { id: 'student-3', name: 'Demo Student 3', role: 'STUDENT' };

describe('CoursesPage', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('shows a loading state while courses are loading', () => {
    vi.stubGlobal('fetch', vi.fn(() => new Promise(() => undefined)));

    render(<CoursesPage />);

    expect(screen.getByText(/Loading courses/i)).toBeInTheDocument();
  });

  it('displays loaded courses from the backend API', async () => {
    vi.stubGlobal('fetch', createCoursesFetchMock({
      courses: [course('course-1', 'Design Patterns CS501', 2, 0)],
      rosters: { 'course-1': [studentOne, studentTwo] },
      students: [studentOne, studentTwo, studentThree],
    }));

    render(<CoursesPage />);

    expect(await screen.findByText('Design Patterns CS501')).toBeInTheDocument();
    expect(screen.getByText(/course-1/i)).toBeInTheDocument();
    expect(screen.getByText(/2 enrolled · 0 assignments/i)).toBeInTheDocument();
    expect(await screen.findByText('Demo Student 1')).toBeInTheDocument();
    expect(screen.getByText('Demo Student 2')).toBeInTheDocument();
  });

  it('creates a course through POST and refreshes the list', async () => {
    const fetchMock = createCoursesFetchMock({
      courses: [],
      coursesAfterCreate: [course('created-course', 'New Backend Course', 0, 0)],
      rosters: { 'created-course': [] },
      students: [studentOne],
    });
    vi.stubGlobal('fetch', fetchMock);
    const user = userEvent.setup();

    render(<CoursesPage />);

    expect(await screen.findByText(/No courses from the backend yet/i)).toBeInTheDocument();
    await user.type(screen.getByLabelText(/Course title/i), 'New Backend Course');
    await user.click(screen.getByRole('button', { name: /Create course/i }));

    expect(await screen.findByText('New Backend Course')).toBeInTheDocument();
    expect(fetchMock).toHaveBeenCalledWith('/api/app/courses', expect.objectContaining({
      method: 'POST',
      body: JSON.stringify({ title: 'New Backend Course' }),
    }));
  });

  it('loads roster data for a selected course', async () => {
    vi.stubGlobal('fetch', createCoursesFetchMock({
      courses: [
        course('course-1', 'First Course', 0, 0),
        course('course-2', 'Second Course', 1, 0),
      ],
      rosters: {
        'course-1': [],
        'course-2': [studentThree],
      },
      students: [studentOne, studentTwo, studentThree],
    }));
    const user = userEvent.setup();

    render(<CoursesPage />);

    await screen.findByText('First Course');
    await user.click(screen.getByRole('button', { name: /Second Course/i }));

    expect(await screen.findByRole('heading', { name: 'Second Course' })).toBeInTheDocument();
    expect(screen.getByText('Demo Student 3')).toBeInTheDocument();
    expect(screen.getByLabelText('Available students')).toHaveTextContent('Demo Student 1');
    expect(screen.getByLabelText('Available students')).toHaveTextContent('Demo Student 2');
  });

  it('enrolls selected students through POST and refreshes roster UI', async () => {
    const fetchMock = createCoursesFetchMock({
      courses: [course('course-1', 'Roster Course', 1, 0)],
      coursesAfterEnrollment: [course('course-1', 'Roster Course', 2, 0)],
      rosters: { 'course-1': [studentOne] },
      rosterAfterEnrollment: { 'course-1': [studentOne, studentTwo] },
      students: [studentOne, studentTwo],
    });
    vi.stubGlobal('fetch', fetchMock);
    const user = userEvent.setup();

    render(<CoursesPage />);

    expect(await screen.findByText('Roster Course')).toBeInTheDocument();
    const availableStudents = await screen.findByLabelText('Available students');
    await user.click(within(availableStudents).getByLabelText('Demo Student 2'));
    await user.click(screen.getByRole('button', { name: /Enroll selected students/i }));

    expect(await screen.findByLabelText('Enrolled students')).toHaveTextContent('Demo Student 2');
    expect(fetchMock).toHaveBeenCalledWith('/api/app/courses/course-1/enrollments', expect.objectContaining({
      method: 'POST',
      body: JSON.stringify({ studentIds: ['student-2'] }),
    }));
  });
});

type FetchMockOptions = {
  courses: ReturnType<typeof course>[];
  coursesAfterCreate?: ReturnType<typeof course>[];
  coursesAfterEnrollment?: ReturnType<typeof course>[];
  rosters: Record<string, typeof studentOne[]>;
  rosterAfterEnrollment?: Record<string, typeof studentOne[]>;
  students: typeof studentOne[];
};

function createCoursesFetchMock(options: FetchMockOptions) {
  let courseList = options.courses;
  let rosterMap = options.rosters;

  return vi.fn((url: string, init?: RequestInit) => {
    if (url === '/api/app/courses' && (!init || init.method === 'GET')) {
      return Promise.resolve(jsonResponse(courseList));
    }

    if (url === '/api/app/courses' && init?.method === 'POST') {
      courseList = options.coursesAfterCreate ?? courseList;
      return Promise.resolve(jsonResponse(courseList.at(-1) ?? course('created-course', 'Created Course', 0, 0)));
    }

    if (url === '/api/app/students') {
      return Promise.resolve(jsonResponse(options.students));
    }

    const detailMatch = url.match(/^\/api\/app\/courses\/([^/]+)$/);
    if (detailMatch) {
      return Promise.resolve(jsonResponse(courseList.find((item) => item.id === detailMatch[1])));
    }

    const rosterMatch = url.match(/^\/api\/app\/courses\/([^/]+)\/roster$/);
    if (rosterMatch) {
      const courseId = rosterMatch[1];
      return Promise.resolve(jsonResponse({ courseId, students: rosterMap[courseId] ?? [] }));
    }

    const enrollmentMatch = url.match(/^\/api\/app\/courses\/([^/]+)\/enrollments$/);
    if (enrollmentMatch && init?.method === 'POST') {
      if (options.coursesAfterEnrollment) {
        courseList = options.coursesAfterEnrollment;
      }
      if (options.rosterAfterEnrollment) {
        rosterMap = options.rosterAfterEnrollment;
      }
      const courseId = enrollmentMatch[1];
      return Promise.resolve(jsonResponse({ courseId, students: rosterMap[courseId] ?? [] }));
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

function jsonResponse(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}
