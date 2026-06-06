import { FormEvent, useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';

import {
  createCourse,
  enrollStudents,
  getCourse,
  getCourseRoster,
  getCourses,
} from '../api/coursesApi';
import { getStudents } from '../api/usersApi';
import type { CourseDetailResponse, CourseResponse, RosterResponse, UserResponse } from '../api/types';

type CoursesPageProps = {
  createFocused?: boolean;
};

export function CoursesPage({ createFocused = false }: CoursesPageProps) {
  const [courses, setCourses] = useState<CourseResponse[]>([]);
  const [selectedCourseId, setSelectedCourseId] = useState<string | null>(null);
  const [courseDetail, setCourseDetail] = useState<CourseDetailResponse | null>(null);
  const [roster, setRoster] = useState<RosterResponse | null>(null);
  const [students, setStudents] = useState<UserResponse[]>([]);
  const [selectedStudentIds, setSelectedStudentIds] = useState<string[]>([]);
  const [newCourseTitle, setNewCourseTitle] = useState('');
  const [isLoadingCourses, setIsLoadingCourses] = useState(true);
  const [isLoadingRoster, setIsLoadingRoster] = useState(false);
  const [isCreating, setIsCreating] = useState(false);
  const [isEnrolling, setIsEnrolling] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    void loadCourses();
  }, []);

  useEffect(() => {
    if (!selectedCourseId) {
      setCourseDetail(null);
      setRoster(null);
      setSelectedStudentIds([]);
      return;
    }

    void loadRosterData(selectedCourseId);
  }, [selectedCourseId]);

  const enrolledStudentIds = useMemo(
    () => new Set(roster?.students.map((student) => student.id) ?? []),
    [roster],
  );
  const availableStudents = useMemo(
    () => students.filter((student) => !enrolledStudentIds.has(student.id)),
    [students, enrolledStudentIds],
  );

  async function loadCourses(nextSelectedCourseId = selectedCourseId) {
    setIsLoadingCourses(true);
    try {
      const courseResponse = await getCourses();
      setCourses(courseResponse);
      setError(null);
      if (nextSelectedCourseId) {
        setSelectedCourseId(nextSelectedCourseId);
      } else if (courseResponse.length > 0 && !selectedCourseId) {
        setSelectedCourseId(courseResponse[0].id);
      }
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load courses');
    } finally {
      setIsLoadingCourses(false);
    }
  }

  async function loadRosterData(courseId: string) {
    setIsLoadingRoster(true);
    try {
      const [detailResponse, rosterResponse, studentResponse] = await Promise.all([
        getCourse(courseId),
        getCourseRoster(courseId),
        getStudents(),
      ]);
      setCourseDetail(detailResponse);
      setRoster(rosterResponse);
      setStudents(studentResponse);
      setSelectedStudentIds([]);
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load course roster');
    } finally {
      setIsLoadingRoster(false);
    }
  }

  async function handleCreateCourse(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const title = newCourseTitle.trim();
    if (!title) {
      setError('Course title is required');
      return;
    }

    setIsCreating(true);
    try {
      const created = await createCourse({ title });
      setNewCourseTitle('');
      await loadCourses(created.id);
      setSelectedCourseId(created.id);
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to create course');
    } finally {
      setIsCreating(false);
    }
  }

  async function handleEnrollStudents() {
    if (!selectedCourseId || selectedStudentIds.length === 0) {
      return;
    }

    setIsEnrolling(true);
    try {
      const updatedRoster = await enrollStudents(selectedCourseId, { studentIds: selectedStudentIds });
      setRoster(updatedRoster);
      setSelectedStudentIds([]);
      await Promise.all([loadCourses(selectedCourseId), loadRosterData(selectedCourseId)]);
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to enroll students');
    } finally {
      setIsEnrolling(false);
    }
  }

  function toggleStudent(studentId: string) {
    setSelectedStudentIds((current) =>
      current.includes(studentId)
        ? current.filter((id) => id !== studentId)
        : [...current, studentId],
    );
  }

  return (
    <section className="page-stack" aria-labelledby="courses-title">
      <div className="hero-card hero-card--compact page-hero">
        <div>
          <p className="eyebrow">{createFocused ? 'Course Builder' : 'Courses'}</p>
          <h2 id="courses-title">{createFocused ? 'Create a backend course' : 'Courses and roster'}</h2>
        </div>
        <p>
          This screen reads and writes course data through the Spring Boot <code>/api/app/courses</code> APIs.
          Roster enrollment uses real seeded students from <code>/api/app/students</code>.
        </p>
      </div>

      {error ? <section className="card error-text">{error}</section> : null}

      <section className="courses-layout">
        <div className="page-stack">
          <section className="card form-card" aria-labelledby="create-course-title">
            <div className="section-heading">
              <div>
                <p className="eyebrow">Create Course</p>
                <h2 id="create-course-title">New course</h2>
              </div>
            </div>
            <form className="inline-form" onSubmit={handleCreateCourse}>
              <label>
                Course title
                <input
                  value={newCourseTitle}
                  onChange={(event) => setNewCourseTitle(event.target.value)}
                  placeholder="Design Patterns CS501"
                />
              </label>
              <button type="submit" disabled={isCreating}>
                {isCreating ? 'Creating...' : 'Create course'}
              </button>
            </form>
          </section>

          <section className="card" aria-labelledby="courses-list-title">
            <div className="section-heading">
              <div>
                <p className="eyebrow">Backend Courses</p>
                <h2 id="courses-list-title">Course list</h2>
              </div>
              <div className="heading-actions">
                <Link to="/assignments">Manage assignments</Link>
                <span className="badge">{courses.length} courses</span>
              </div>
            </div>

            {isLoadingCourses ? <p className="muted">Loading courses...</p> : null}
            {!isLoadingCourses && courses.length === 0 ? (
              <p className="muted">No courses from the backend yet. Create one to begin.</p>
            ) : null}
            {!isLoadingCourses && courses.length > 0 ? (
              <div className="course-list" aria-label="Courses">
                {courses.map((course) => (
                  <button
                    className={`course-card ${selectedCourseId === course.id ? 'course-card--selected' : ''}`}
                    key={course.id}
                    type="button"
                    onClick={() => setSelectedCourseId(course.id)}
                  >
                    <div className="course-card__body">
                      <span className="card-icon" aria-hidden="true">CO</span>
                      <div>
                        <strong>{course.title}</strong>
                        <span>{course.id}</span>
                        <small>
                          {course.rosterCount} enrolled · {course.assignmentCount} assignments
                        </small>
                      </div>
                    </div>
                    <span className="badge badge--success">Active</span>
                  </button>
                ))}
              </div>
            ) : null}
          </section>
        </div>

        <section className="card roster-panel" aria-labelledby="roster-title">
          <div className="section-heading">
            <div>
              <p className="eyebrow">Roster</p>
              <h2 id="roster-title">{courseDetail ? courseDetail.title : 'Select a course'}</h2>
            </div>
            {courseDetail ? <span className="badge">{courseDetail.rosterCount} enrolled</span> : null}
          </div>

          {!selectedCourseId ? <p className="muted">Select a course to view and manage its roster.</p> : null}
          {selectedCourseId && isLoadingRoster ? <p className="muted">Loading roster...</p> : null}
          {selectedCourseId && !isLoadingRoster && roster ? (
            <div className="page-stack">
              <div>
                <h3>Enrolled students</h3>
                {roster.students.length === 0 ? (
                  <p className="muted">No students enrolled yet.</p>
                ) : (
                  <ul className="compact-list" aria-label="Enrolled students">
                    {roster.students.map((student) => (
                      <li key={student.id}>
                        <span>{student.name}</span>
                        <small>{student.role}</small>
                      </li>
                    ))}
                  </ul>
                )}
              </div>

              <div>
                <h3>Available students</h3>
                {availableStudents.length === 0 ? (
                  <p className="muted">All backend students are already enrolled in this course.</p>
                ) : (
                  <div className="checkbox-list" aria-label="Available students">
                    {availableStudents.map((student) => (
                      <label key={student.id}>
                        <input
                          type="checkbox"
                          checked={selectedStudentIds.includes(student.id)}
                          onChange={() => toggleStudent(student.id)}
                        />
                        <span>{student.name}</span>
                      </label>
                    ))}
                  </div>
                )}
                <button
                  type="button"
                  disabled={selectedStudentIds.length === 0 || isEnrolling}
                  onClick={handleEnrollStudents}
                >
                  {isEnrolling ? 'Enrolling...' : 'Enroll selected students'}
                </button>
              </div>
            </div>
          ) : null}
        </section>
      </section>
    </section>
  );
}
