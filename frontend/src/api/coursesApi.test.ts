import { afterEach, describe, expect, it, vi } from 'vitest';

import { createCourse, enrollStudents, getCourse, getCourseRoster, getCourses } from './coursesApi';

describe('coursesApi', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('calls the courses endpoint for course listing', async () => {
    const fetchMock = vi.fn().mockResolvedValue(jsonResponse([]));
    vi.stubGlobal('fetch', fetchMock);

    await getCourses();

    expect(fetchMock).toHaveBeenCalledWith('/api/app/courses', {
      method: 'GET',
      headers: { Accept: 'application/json' },
    });
  });

  it('calls course create, detail, roster, and enrollment endpoints', async () => {
    const fetchMock = vi.fn().mockImplementation(() => Promise.resolve(jsonResponse({})));
    vi.stubGlobal('fetch', fetchMock);

    await createCourse({ title: 'Design Patterns CS501' });
    await getCourse('course-1');
    await getCourseRoster('course-1');
    await enrollStudents('course-1', { studentIds: ['student-1'] });

    expect(fetchMock).toHaveBeenNthCalledWith(1, '/api/app/courses', {
      method: 'POST',
      headers: { Accept: 'application/json', 'Content-Type': 'application/json' },
      body: JSON.stringify({ title: 'Design Patterns CS501' }),
    });
    expect(fetchMock).toHaveBeenNthCalledWith(2, '/api/app/courses/course-1', {
      method: 'GET',
      headers: { Accept: 'application/json' },
    });
    expect(fetchMock).toHaveBeenNthCalledWith(3, '/api/app/courses/course-1/roster', {
      method: 'GET',
      headers: { Accept: 'application/json' },
    });
    expect(fetchMock).toHaveBeenNthCalledWith(4, '/api/app/courses/course-1/enrollments', {
      method: 'POST',
      headers: { Accept: 'application/json', 'Content-Type': 'application/json' },
      body: JSON.stringify({ studentIds: ['student-1'] }),
    });
  });
});

function jsonResponse(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}
