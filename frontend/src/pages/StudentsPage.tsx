import { useEffect, useState } from 'react';

import { getStudents } from '../api/usersApi';
import type { UserResponse } from '../api/types';

export function StudentsPage() {
  const [students, setStudents] = useState<UserResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    getStudents()
      .then((studentResponse) => {
        if (isMounted) {
          setStudents(studentResponse);
          setError(null);
        }
      })
      .catch((caughtError: unknown) => {
        if (isMounted) {
          setError(caughtError instanceof Error ? caughtError.message : 'Unable to load students');
        }
      })
      .finally(() => {
        if (isMounted) {
          setIsLoading(false);
        }
      });

    return () => {
      isMounted = false;
    };
  }, []);

  return (
    <section className="page-stack" aria-labelledby="students-title">
      <div className="hero-card hero-card--compact page-hero">
        <div>
          <p className="eyebrow">Student Roster</p>
          <h2 id="students-title">Enrolled students</h2>
        </div>
        <p>
          Everyone currently enrolled across your courses. Manage enrollment from the Courses page.
        </p>
      </div>

      {isLoading ? <section className="card">Loading students...</section> : null}
      {error ? <section className="card error-text">{error}</section> : null}
      {!isLoading && !error ? (
        <section className="student-grid student-grid--roster" aria-label="Enrolled students">
          {students.map((student) => (
            <article className="student-card" key={student.id}>
              <div className="avatar" aria-hidden="true">
                {initials(student.name)}
              </div>
              <div>
                <h3>{student.name}</h3>
                <p>{student.role}</p>
                <small>No email on file</small>
              </div>
              <span className="badge badge--success">Active</span>
            </article>
          ))}
        </section>
      ) : null}
    </section>
  );
}

function initials(name: string): string {
  return name
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase())
    .join('');
}
