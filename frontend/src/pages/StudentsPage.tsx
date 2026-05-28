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
      <div className="hero-card hero-card--compact">
        <p className="eyebrow">Student Roster</p>
        <h2 id="students-title">Seeded students from the backend</h2>
        <p>
          This page reads only from <code>/api/app/students</code>. Enrollment workflows will be wired in a later frontend phase.
        </p>
      </div>

      {isLoading ? <section className="card">Loading students...</section> : null}
      {error ? <section className="card error-text">{error}</section> : null}
      {!isLoading && !error ? (
        <section className="student-grid" aria-label="Seeded students">
          {students.map((student) => (
            <article className="student-card" key={student.id}>
              <div className="avatar" aria-hidden="true">
                {initials(student.name)}
              </div>
              <div>
                <h3>{student.name}</h3>
                <p>{student.role}</p>
                <small>Email not provided by backend API</small>
              </div>
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
