import { FormEvent, useEffect, useMemo, useState } from 'react';

import { getCourseAssignments } from '../api/assignmentsApi';
import { getCourses } from '../api/coursesApi';
import {
  analyzeSubmission,
  createSubmission,
  getAssignmentSubmissions,
  getSubmission,
} from '../api/submissionsApi';
import { getStudents } from '../api/usersApi';
import { TracePanel } from '../components/trace/TracePanel';
import type {
  AIAnalysisReportResponse,
  AssignmentResponse,
  CourseResponse,
  CreateSubmissionRequest,
  SubmissionDetailResponse,
  SubmissionListItemResponse,
  SubmissionType,
  UserResponse,
} from '../api/types';

export function SubmissionsPage() {
  const [courses, setCourses] = useState<CourseResponse[]>([]);
  const [assignments, setAssignments] = useState<AssignmentResponse[]>([]);
  const [submissions, setSubmissions] = useState<SubmissionListItemResponse[]>([]);
  const [students, setStudents] = useState<UserResponse[]>([]);
  const [selectedCourseId, setSelectedCourseId] = useState('');
  const [selectedAssignmentId, setSelectedAssignmentId] = useState('');
  const [selectedSubmissionId, setSelectedSubmissionId] = useState('');
  const [submissionDetail, setSubmissionDetail] = useState<SubmissionDetailResponse | null>(null);
  const [studentId, setStudentId] = useState('');
  const [submissionType, setSubmissionType] = useState<SubmissionType>('PDF_TEXT');
  const [content, setContent] = useState('');
  const [isLoadingCourses, setIsLoadingCourses] = useState(true);
  const [isLoadingAssignments, setIsLoadingAssignments] = useState(false);
  const [isLoadingSubmissions, setIsLoadingSubmissions] = useState(false);
  const [isLoadingDetail, setIsLoadingDetail] = useState(false);
  const [isCreating, setIsCreating] = useState(false);
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [traceRefreshKey, setTraceRefreshKey] = useState(0);
  const [error, setError] = useState<string | null>(null);

  const selectedAssignment = useMemo(
    () => assignments.find((assignment) => assignment.id === selectedAssignmentId) ?? null,
    [assignments, selectedAssignmentId],
  );

  useEffect(() => {
    void loadInitialData();
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
      setSubmissionDetail(null);
      return;
    }

    void loadSubmissionDetail(selectedSubmissionId);
  }, [selectedSubmissionId]);

  async function loadInitialData() {
    setIsLoadingCourses(true);
    try {
      const [courseResponse, studentResponse] = await Promise.all([getCourses(), getStudents()]);
      setCourses(courseResponse);
      setStudents(studentResponse);
      setStudentId(studentResponse[0]?.id ?? '');
      setError(null);
      if (courseResponse.length > 0) {
        setSelectedCourseId(courseResponse[0].id);
      }
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load courses and students');
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
      setSubmissionDetail(null);
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load assignments');
    } finally {
      setIsLoadingAssignments(false);
    }
  }

  async function loadSubmissions(assignmentId: string, nextSelectedSubmissionId = '') {
    setIsLoadingSubmissions(true);
    try {
      const submissionResponse = await getAssignmentSubmissions(assignmentId);
      setSubmissions(submissionResponse);
      setSelectedSubmissionId(nextSelectedSubmissionId);
      if (!nextSelectedSubmissionId) {
        setSubmissionDetail(null);
      }
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load submissions');
    } finally {
      setIsLoadingSubmissions(false);
    }
  }

  async function loadSubmissionDetail(submissionId: string) {
    setIsLoadingDetail(true);
    try {
      const detailResponse = await getSubmission(submissionId);
      setSubmissionDetail(detailResponse);
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load submission detail');
    } finally {
      setIsLoadingDetail(false);
    }
  }

  async function handleCreateSubmission(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!selectedAssignmentId || !studentId || !content.trim()) {
      setError('Select an assignment and student, then enter submission content');
      return;
    }

    const request: CreateSubmissionRequest = {
      studentId,
      submissionType,
      content: content.trim(),
    };

    setIsCreating(true);
    try {
      const created = await createSubmission(selectedAssignmentId, request);
      setContent('');
      await loadSubmissions(selectedAssignmentId, created.id);
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to create submission');
    } finally {
      setIsCreating(false);
    }
  }

  async function handleAnalyzeSubmission() {
    if (!selectedSubmissionId || !selectedAssignmentId) {
      return;
    }

    setIsAnalyzing(true);
    try {
      await analyzeSubmission(selectedSubmissionId);
      await loadSubmissions(selectedAssignmentId, selectedSubmissionId);
      await loadSubmissionDetail(selectedSubmissionId);
      setTraceRefreshKey((current) => current + 1);
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to run Mock AI Analysis');
    } finally {
      setIsAnalyzing(false);
    }
  }

  return (
    <section className="page-stack" aria-labelledby="submissions-title">
      <div className="hero-card hero-card--compact">
        <p className="eyebrow">Submissions</p>
        <h2 id="submissions-title">Submissions and Mock AI Analysis</h2>
        <p>
          Create backend submissions, then explicitly run Mock AI Analysis or the Mock Java sandbox/test runner through the
          existing Spring Boot APIs.
        </p>
      </div>

      {error ? <section className="card error-text">{error}</section> : null}

      <section className="submissions-layout">
        <div className="page-stack">
          <section className="card" aria-labelledby="submission-selector-title">
            <div className="section-heading">
              <div>
                <p className="eyebrow">Backend Assignment</p>
                <h2 id="submission-selector-title">Select assignment</h2>
              </div>
            </div>

            <div className="form-grid">
              <label className="field">
                Course
                <select
                  value={selectedCourseId}
                  onChange={(event) => setSelectedCourseId(event.target.value)}
                  disabled={isLoadingCourses}
                >
                  {courses.length === 0 ? <option value="">No backend courses yet</option> : null}
                  {courses.map((course) => (
                    <option value={course.id} key={course.id}>
                      {course.title}
                    </option>
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
                    <option value={assignment.id} key={assignment.id}>
                      {assignment.title}
                    </option>
                  ))}
                </select>
              </label>
            </div>
          </section>

          <section className="card" aria-labelledby="submission-list-title">
            <div className="section-heading">
              <div>
                <p className="eyebrow">Backend Submissions</p>
                <h2 id="submission-list-title">Submission list</h2>
              </div>
              <span className="badge">{submissions.length} submissions</span>
            </div>

            {isLoadingCourses || isLoadingAssignments || isLoadingSubmissions ? (
              <p className="muted">Loading submissions...</p>
            ) : null}
            {!isLoadingCourses && selectedCourseId && assignments.length === 0 ? (
              <p className="muted">This course has no backend assignments yet.</p>
            ) : null}
            {!isLoadingSubmissions && selectedAssignmentId && submissions.length === 0 ? (
              <p className="muted">No submissions from the backend for this assignment yet.</p>
            ) : null}
            {!selectedCourseId ? <p className="muted">Create a course and assignment before adding submissions.</p> : null}

            {submissions.length > 0 ? (
              <div className="submission-list" aria-label="Submissions">
                {submissions.map((submission) => (
                  <button
                    className={`submission-card ${selectedSubmissionId === submission.id ? 'submission-card--selected' : ''}`}
                    type="button"
                    key={submission.id}
                    onClick={() => setSelectedSubmissionId(submission.id)}
                  >
                    <strong>{submission.student.name}</strong>
                    <span>{submission.id}</span>
                    <small>
                      {submission.type} · {submission.status} · {formatDateTime(submission.submittedAt)}
                    </small>
                    <small>{submission.hasAnalysisReport ? 'Analysis report exists' : 'No analysis report yet'}</small>
                  </button>
                ))}
              </div>
            ) : null}
          </section>

          <section className="card" aria-labelledby="create-submission-title">
            <div className="section-heading">
              <div>
                <p className="eyebrow">Create Submission</p>
                <h2 id="create-submission-title">New submission</h2>
              </div>
            </div>

            <form className="assignment-form" onSubmit={handleCreateSubmission}>
              <label className="field">
                Student
                <select value={studentId} onChange={(event) => setStudentId(event.target.value)}>
                  {students.length === 0 ? <option value="">No backend students yet</option> : null}
                  {students.map((student) => (
                    <option value={student.id} key={student.id}>
                      {student.name}
                    </option>
                  ))}
                </select>
              </label>

              <label className="field">
                Submission type
                <select value={submissionType} onChange={(event) => setSubmissionType(event.target.value as SubmissionType)}>
                  <option value="PDF_TEXT">PDF/Text</option>
                  <option value="JAVA_CODE">Java Code</option>
                </select>
              </label>

              <label className="field">
                Content
                <textarea
                  className="code-textarea"
                  value={content}
                  onChange={(event) => setContent(event.target.value)}
                  placeholder={submissionType === 'JAVA_CODE' ? 'public class AdapterSubmission { ... }' : 'Paste PDF/Text submission content'}
                />
              </label>

              <button type="submit" disabled={isCreating || !selectedAssignmentId}>
                {isCreating ? 'Creating submission...' : 'Create submission'}
              </button>
              <p className="muted">Creating a submission does not automatically run analysis.</p>
            </form>
          </section>
        </div>

        <div className="page-stack">
          <section className="card submission-detail-panel" aria-labelledby="submission-detail-title">
            <div className="section-heading">
              <div>
                <p className="eyebrow">Submission Detail</p>
                <h2 id="submission-detail-title">Review and analyze</h2>
              </div>
            </div>

            {!selectedSubmissionId ? <p className="muted">Select a submission to view details.</p> : null}
            {isLoadingDetail ? <p className="muted">Loading submission detail...</p> : null}
            {submissionDetail && !isLoadingDetail ? (
              <div className="page-stack">
                <dl className="detail-list">
                  <div>
                    <dt>Student</dt>
                    <dd>{submissionDetail.student.name}</dd>
                  </div>
                  <div>
                    <dt>Assignment</dt>
                    <dd>{selectedAssignment?.title ?? submissionDetail.assignmentId}</dd>
                  </div>
                  <div>
                    <dt>Type</dt>
                    <dd>{submissionDetail.type}</dd>
                  </div>
                  <div>
                    <dt>Status</dt>
                    <dd>{submissionDetail.status}</dd>
                  </div>
                  <div>
                    <dt>Submitted</dt>
                    <dd>{formatDateTime(submissionDetail.submittedAt)}</dd>
                  </div>
                  <div>
                    <dt>Content preview</dt>
                    <dd>Not returned by the current backend detail API.</dd>
                  </div>
                </dl>

                {!submissionDetail.report ? (
                  <button type="button" onClick={handleAnalyzeSubmission} disabled={isAnalyzing}>
                    {isAnalyzing ? 'Running Mock AI Analysis...' : 'Run Mock AI Analysis'}
                  </button>
                ) : (
                  <AnalysisReport report={submissionDetail.report} />
                )}
              </div>
            ) : null}
          </section>

          <TracePanel key={traceRefreshKey} title="Trace After Analysis" limit={5} />
        </div>
      </section>
    </section>
  );
}

function AnalysisReport({ report }: { report: AIAnalysisReportResponse }) {
  return (
    <section className="analysis-report" aria-labelledby="analysis-report-title">
      <div>
        <p className="eyebrow">Mock AI Analysis Report</p>
        <h3 id="analysis-report-title">Mock AI Analysis Report</h3>
      </div>
      <p>{report.summary}</p>

      <div>
        <h4>Rubric findings</h4>
        {report.rubricFindings.length === 0 ? (
          <p className="muted">No rubric findings returned.</p>
        ) : (
          <ul className="compact-list">
            {report.rubricFindings.map((finding) => (
              <li key={finding.criterionId}>
                <span>{finding.feedback}</span>
                <small>{finding.pointsEarned} points</small>
              </li>
            ))}
          </ul>
        )}
      </div>

      <div>
        <h4>Mock Java sandbox/test runner results</h4>
        {report.testResults.length === 0 ? (
          <p className="muted">No Java code test results returned.</p>
        ) : (
          <ul className="compact-list">
            {report.testResults.map((result) => (
              <li key={result.testName}>
                <span>{result.testName}</span>
                <small>{result.passed ? 'Passed' : 'Failed'} · {result.output}</small>
              </li>
            ))}
          </ul>
        )}
      </div>

      <div>
        <h4>Suggested feedback</h4>
        <p>{report.suggestedFeedback}</p>
      </div>

      {report.gradeSuggestion ? (
        <div>
          <h4>Grade suggestion</h4>
          <p>
            {report.gradeSuggestion.points}/{report.gradeSuggestion.maxPoints}: {report.gradeSuggestion.explanation}
          </p>
        </div>
      ) : null}
    </section>
  );
}

function formatDateTime(value: string) {
  return new Intl.DateTimeFormat(undefined, {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value));
}
