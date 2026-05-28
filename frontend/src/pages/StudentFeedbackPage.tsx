import { useEffect, useState } from 'react';

import { getCourseAssignments } from '../api/assignmentsApi';
import { getCourses } from '../api/coursesApi';
import { getStudentFeedback } from '../api/feedbackApi';
import { getAssignmentSubmissions } from '../api/submissionsApi';
import type {
  AssignmentResponse,
  CourseResponse,
  StudentFeedbackResponse,
  SubmissionListItemResponse,
} from '../api/types';

export function StudentFeedbackPage() {
  const [courses, setCourses] = useState<CourseResponse[]>([]);
  const [assignments, setAssignments] = useState<AssignmentResponse[]>([]);
  const [submissions, setSubmissions] = useState<SubmissionListItemResponse[]>([]);
  const [selectedCourseId, setSelectedCourseId] = useState('');
  const [selectedAssignmentId, setSelectedAssignmentId] = useState('');
  const [selectedSubmissionId, setSelectedSubmissionId] = useState('');
  const [feedback, setFeedback] = useState<StudentFeedbackResponse | null>(null);
  const [isLoadingCourses, setIsLoadingCourses] = useState(true);
  const [isLoadingAssignments, setIsLoadingAssignments] = useState(false);
  const [isLoadingSubmissions, setIsLoadingSubmissions] = useState(false);
  const [isLoadingFeedback, setIsLoadingFeedback] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [feedbackMessage, setFeedbackMessage] = useState<string | null>(null);

  useEffect(() => {
    void loadCourses();
  }, []);

  useEffect(() => {
    if (!selectedCourseId) {
      setAssignments([]);
      setSelectedAssignmentId('');
      return;
    }
    void loadAssignments(selectedCourseId);
  }, [selectedCourseId]);

  useEffect(() => {
    if (!selectedAssignmentId) {
      setSubmissions([]);
      setSelectedSubmissionId('');
      return;
    }
    void loadSubmissions(selectedAssignmentId);
  }, [selectedAssignmentId]);

  useEffect(() => {
    if (!selectedSubmissionId) {
      setFeedback(null);
      setFeedbackMessage(null);
      return;
    }
    void loadStudentFeedback(selectedSubmissionId);
  }, [selectedSubmissionId]);

  async function loadCourses() {
    setIsLoadingCourses(true);
    try {
      const courseResponse = await getCourses();
      setCourses(courseResponse);
      setSelectedCourseId(courseResponse[0]?.id ?? '');
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load courses');
    } finally {
      setIsLoadingCourses(false);
    }
  }

  async function loadAssignments(courseId: string) {
    setIsLoadingAssignments(true);
    try {
      const assignmentResponse = await getCourseAssignments(courseId);
      setAssignments(assignmentResponse);
      setSelectedAssignmentId(assignmentResponse[0]?.id ?? '');
      setSelectedSubmissionId('');
      setFeedback(null);
      setFeedbackMessage(null);
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load assignments');
    } finally {
      setIsLoadingAssignments(false);
    }
  }

  async function loadSubmissions(assignmentId: string) {
    setIsLoadingSubmissions(true);
    try {
      const submissionResponse = await getAssignmentSubmissions(assignmentId);
      setSubmissions(submissionResponse);
      setSelectedSubmissionId(submissionResponse[0]?.id ?? '');
      setFeedback(null);
      setFeedbackMessage(null);
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load submissions');
    } finally {
      setIsLoadingSubmissions(false);
    }
  }

  async function loadStudentFeedback(submissionId: string) {
    setIsLoadingFeedback(true);
    try {
      const feedbackResponse = await getStudentFeedback(submissionId);
      setFeedback(feedbackResponse);
      setFeedbackMessage(null);
      setError(null);
    } catch (caughtError) {
      setFeedback(null);
      setFeedbackMessage(
        caughtError instanceof Error
          ? `Student feedback is not finalized or unavailable: ${caughtError.message}`
          : 'Student feedback is not finalized or unavailable.',
      );
    } finally {
      setIsLoadingFeedback(false);
    }
  }

  return (
    <section className="page-stack" aria-labelledby="student-feedback-title">
      <div className="hero-card hero-card--compact page-hero">
        <div>
          <p className="eyebrow">Student Feedback</p>
          <h2 id="student-feedback-title">Finalized student feedback</h2>
        </div>
        <p>
          Select a backend submission and render the finalized feedback returned by the student feedback API. Drafts and
          unfinalized responses are shown as unavailable instead of mocked.
        </p>
      </div>

      {error ? <section className="card error-text">{error}</section> : null}

      <section className="feedback-layout">
        <section className="card" aria-labelledby="student-feedback-selection-title">
          <div className="section-heading">
            <div>
              <p className="eyebrow">Selection</p>
              <h2 id="student-feedback-selection-title">Choose submission</h2>
            </div>
          </div>

          <div className="form-grid">
            <label className="field">
              Course
              <select value={selectedCourseId} onChange={(event) => setSelectedCourseId(event.target.value)} disabled={isLoadingCourses}>
                {courses.length === 0 ? <option value="">No backend courses yet</option> : null}
                {courses.map((course) => (
                  <option value={course.id} key={course.id}>{course.title}</option>
                ))}
              </select>
            </label>
            <label className="field">
              Assignment
              <select
                value={selectedAssignmentId}
                onChange={(event) => setSelectedAssignmentId(event.target.value)}
                disabled={isLoadingAssignments || assignments.length === 0}
              >
                {assignments.length === 0 ? <option value="">No backend assignments yet</option> : null}
                {assignments.map((assignment) => (
                  <option value={assignment.id} key={assignment.id}>{assignment.title}</option>
                ))}
              </select>
            </label>
          </div>

          {isLoadingCourses || isLoadingAssignments || isLoadingSubmissions ? <p className="muted">Loading student feedback options...</p> : null}
          {!isLoadingAssignments && selectedCourseId && assignments.length === 0 ? <p className="muted">This course has no backend assignments yet.</p> : null}
          {!isLoadingSubmissions && selectedAssignmentId && submissions.length === 0 ? <p className="muted">No submissions from the backend for this assignment yet.</p> : null}

          {submissions.length > 0 ? (
            <div className="submission-list" aria-label="Student feedback submissions">
              {submissions.map((submission) => (
                <button
                  className={`submission-card ${selectedSubmissionId === submission.id ? 'submission-card--selected' : ''}`}
                  type="button"
                  key={submission.id}
                  onClick={() => setSelectedSubmissionId(submission.id)}
                >
                  <div className="entity-card__header">
                    <span className="card-icon card-icon--accent" aria-hidden="true">SF</span>
                    <div>
                      <strong>{submission.student.name}</strong>
                      <span>{submission.id}</span>
                    </div>
                    <span className={`badge ${submission.hasAnalysisReport ? 'badge--accent' : 'badge--muted'}`}>
                      Status: {submission.status}
                    </span>
                  </div>
                  <small>
                    {submission.type} · {submission.status} · {submission.hasAnalysisReport ? 'Has analysis' : 'No analysis yet'}
                  </small>
                </button>
              ))}
            </div>
          ) : null}
        </section>

        <section className="card" aria-labelledby="student-feedback-result-title">
          <div className="section-heading">
            <div>
              <p className="eyebrow">Final Response</p>
              <h2 id="student-feedback-result-title">Student view</h2>
            </div>
          </div>

          {!selectedSubmissionId ? <p className="muted">Select a submission to load finalized student feedback.</p> : null}
          {isLoadingFeedback ? <p className="muted">Loading finalized student feedback...</p> : null}
          {feedbackMessage ? <p className="muted empty-state-card">{feedbackMessage}</p> : null}

          {feedback ? (
            <div className="page-stack">
              <section className="analysis-report">
                <h3>Final feedback</h3>
                <p>{feedback.finalFeedback}</p>
                <p>
                  Grade: {feedback.grade.points}/{feedback.grade.maxPoints} · {feedback.grade.explanation}
                </p>
              </section>

              <section className="analysis-report">
                <h3>Mock AI summary</h3>
                <p>{feedback.aiSummary}</p>
                <p className="muted">
                  Mock Java sandbox/test runner results: {feedback.report.testResults.length}
                </p>
              </section>

              {feedback.notification ? (
                <section className="analysis-report">
                  <h3>Notification</h3>
                  <p>{feedback.notification.message}</p>
                  <p className="muted">{formatDateTime(feedback.notification.createdAt)}</p>
                </section>
              ) : null}
            </div>
          ) : null}
        </section>
      </section>
    </section>
  );
}

function formatDateTime(value: string) {
  return new Intl.DateTimeFormat(undefined, {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value));
}
