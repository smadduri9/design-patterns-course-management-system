import { FormEvent, useEffect, useMemo, useState } from 'react';

import { getCourseAssignments } from '../api/assignmentsApi';
import { getCourses } from '../api/coursesApi';
import {
  finalizeFeedback,
  getFeedbackDrafts,
  restoreFeedbackDraft,
  saveFeedbackDraft,
} from '../api/feedbackApi';
import { getAssignmentSubmissions, getSubmission } from '../api/submissionsApi';
import { TracePanel } from '../components/trace/TracePanel';
import type {
  AssignmentResponse,
  CourseResponse,
  FeedbackDraftResponse,
  FinalFeedbackResponse,
  SubmissionDetailResponse,
  SubmissionListItemResponse,
} from '../api/types';

export function FeedbackReviewPage() {
  const [courses, setCourses] = useState<CourseResponse[]>([]);
  const [assignments, setAssignments] = useState<AssignmentResponse[]>([]);
  const [submissions, setSubmissions] = useState<SubmissionListItemResponse[]>([]);
  const [selectedCourseId, setSelectedCourseId] = useState('');
  const [selectedAssignmentId, setSelectedAssignmentId] = useState('');
  const [selectedSubmissionId, setSelectedSubmissionId] = useState('');
  const [submissionDetail, setSubmissionDetail] = useState<SubmissionDetailResponse | null>(null);
  const [drafts, setDrafts] = useState<FeedbackDraftResponse | null>(null);
  const [feedbackText, setFeedbackText] = useState('');
  const [finalResult, setFinalResult] = useState<FinalFeedbackResponse | null>(null);
  const [isLoadingCourses, setIsLoadingCourses] = useState(true);
  const [isLoadingAssignments, setIsLoadingAssignments] = useState(false);
  const [isLoadingSubmissions, setIsLoadingSubmissions] = useState(false);
  const [isLoadingFeedback, setIsLoadingFeedback] = useState(false);
  const [isSavingDraft, setIsSavingDraft] = useState(false);
  const [isFinalizing, setIsFinalizing] = useState(false);
  const [traceRefreshKey, setTraceRefreshKey] = useState(0);
  const [error, setError] = useState<string | null>(null);

  const selectedAssignment = useMemo(
    () => assignments.find((assignment) => assignment.id === selectedAssignmentId) ?? null,
    [assignments, selectedAssignmentId],
  );
  const canEditFeedback = Boolean(submissionDetail?.hasAnalysisReport || submissionDetail?.status === 'AWAITING_REVIEW' || submissionDetail?.status === 'FINALIZED');

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
      resetFeedbackState();
      return;
    }
    void loadFeedbackState(selectedSubmissionId);
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
      resetFeedbackState();
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
      setSelectedSubmissionId('');
      resetFeedbackState();
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load submissions');
    } finally {
      setIsLoadingSubmissions(false);
    }
  }

  async function loadFeedbackState(submissionId: string) {
    setIsLoadingFeedback(true);
    try {
      const [detailResponse, draftResponse] = await Promise.all([
        getSubmission(submissionId),
        getFeedbackDrafts(submissionId),
      ]);
      setSubmissionDetail(detailResponse);
      setDrafts(draftResponse);
      setFeedbackText(draftResponse.currentFeedback ?? '');
      setFinalResult(null);
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load feedback state');
    } finally {
      setIsLoadingFeedback(false);
    }
  }

  async function handleSaveDraft(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!selectedSubmissionId || !feedbackText.trim()) {
      setError('Select a submission and enter feedback text before saving a draft');
      return;
    }

    setIsSavingDraft(true);
    try {
      const draftResponse = await saveFeedbackDraft(selectedSubmissionId, { feedbackText: feedbackText.trim() });
      setDrafts(draftResponse);
      setFeedbackText(draftResponse.currentFeedback ?? '');
      await loadSubmissionDetailOnly(selectedSubmissionId);
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to save feedback draft');
    } finally {
      setIsSavingDraft(false);
    }
  }

  async function handleRestoreDraft(draftIndex: number) {
    if (!selectedSubmissionId) {
      return;
    }

    setIsSavingDraft(true);
    try {
      const draftResponse = await restoreFeedbackDraft(selectedSubmissionId, { draftIndex });
      setDrafts(draftResponse);
      setFeedbackText(draftResponse.currentFeedback ?? '');
      await loadSubmissionDetailOnly(selectedSubmissionId);
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to restore feedback draft');
    } finally {
      setIsSavingDraft(false);
    }
  }

  async function handleFinalizeFeedback() {
    if (!selectedSubmissionId || !feedbackText.trim()) {
      setError('Select a submission and enter feedback text before sending final feedback');
      return;
    }

    setIsFinalizing(true);
    try {
      const result = await finalizeFeedback(selectedSubmissionId, { feedbackText: feedbackText.trim() });
      setFinalResult(result);
      await Promise.all([
        loadSubmissionDetailOnly(selectedSubmissionId),
        selectedAssignmentId ? loadSubmissionsPreservingSelection(selectedAssignmentId, selectedSubmissionId) : Promise.resolve(),
      ]);
      setTraceRefreshKey((current) => current + 1);
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to send final feedback');
    } finally {
      setIsFinalizing(false);
    }
  }

  async function loadSubmissionDetailOnly(submissionId: string) {
    const detailResponse = await getSubmission(submissionId);
    setSubmissionDetail(detailResponse);
  }

  async function loadSubmissionsPreservingSelection(assignmentId: string, submissionId: string) {
    const submissionResponse = await getAssignmentSubmissions(assignmentId);
    setSubmissions(submissionResponse);
    setSelectedSubmissionId(submissionId);
  }

  function resetFeedbackState() {
    setSubmissionDetail(null);
    setDrafts(null);
    setFeedbackText('');
    setFinalResult(null);
  }

  return (
    <section className="page-stack" aria-labelledby="feedback-title">
      <div className="hero-card hero-card--compact page-hero">
        <div>
          <p className="eyebrow">Feedback Review</p>
          <h2 id="feedback-title">Instructor feedback review</h2>
        </div>
        <p>
          Review backend analysis results, save feedback drafts, restore draft snapshots, and send final feedback through
          the Spring Boot feedback APIs.
        </p>
      </div>

      {error ? <section className="card error-text">{error}</section> : null}

      <section className="feedback-layout">
        <div className="page-stack">
          <section className="card" aria-labelledby="feedback-selection-title">
            <div className="section-heading">
              <div>
                <p className="eyebrow">Selection</p>
                <h2 id="feedback-selection-title">Choose submission</h2>
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

            {isLoadingCourses || isLoadingAssignments || isLoadingSubmissions ? <p className="muted">Loading feedback review data...</p> : null}
            {!isLoadingAssignments && selectedCourseId && assignments.length === 0 ? <p className="muted">This course has no backend assignments yet.</p> : null}
            {!isLoadingSubmissions && selectedAssignmentId && submissions.length === 0 ? <p className="muted">No submissions from the backend for this assignment yet.</p> : null}

            {submissions.length > 0 ? (
              <div className="submission-list" aria-label="Feedback submissions">
                {submissions.map((submission) => (
                  <button
                    className={`submission-card ${selectedSubmissionId === submission.id ? 'submission-card--selected' : ''}`}
                    type="button"
                    key={submission.id}
                    onClick={() => setSelectedSubmissionId(submission.id)}
                  >
                    <div className="entity-card__header">
                      <span className="card-icon card-icon--accent" aria-hidden="true">FR</span>
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

          <section className="card form-card" aria-labelledby="drafts-title">
            <div className="section-heading">
              <div>
                <p className="eyebrow">Memento Drafts</p>
                <h2 id="drafts-title">Feedback drafts</h2>
              </div>
              {drafts ? <span className="badge">{drafts.drafts.length} drafts</span> : null}
            </div>

            {!selectedSubmissionId ? <p className="muted">Select a submission to load feedback drafts.</p> : null}
            {isLoadingFeedback ? <p className="muted">Loading feedback drafts...</p> : null}
            {selectedSubmissionId && submissionDetail && !canEditFeedback ? (
              <p className="muted">Run Mock AI Analysis before saving or sending instructor feedback.</p>
            ) : null}

            {selectedSubmissionId && drafts ? (
              <form className="assignment-form" onSubmit={handleSaveDraft}>
                <label className="field">
                  Current feedback
                  <textarea
                    value={feedbackText}
                    onChange={(event) => setFeedbackText(event.target.value)}
                    placeholder="Use the Mock AI analysis summary and rubric findings to draft feedback."
                  />
                </label>
                <div className="button-row">
                  <button type="submit" disabled={!canEditFeedback || isSavingDraft}>
                    {isSavingDraft ? 'Saving draft...' : 'Save Draft'}
                  </button>
                  <button type="button" disabled={!canEditFeedback || isFinalizing} onClick={handleFinalizeFeedback}>
                    {isFinalizing ? 'Sending final feedback...' : 'Send Final Feedback'}
                  </button>
                </div>
              </form>
            ) : null}

            {drafts && drafts.drafts.length > 0 ? (
              <ul className="compact-list" aria-label="Feedback draft snapshots">
                {drafts.drafts.map((draft) => (
                  <li key={draft.index}>
                    <span>{draft.feedbackText}</span>
                    <small>{formatDateTime(draft.savedAt)}</small>
                    <button type="button" disabled={!canEditFeedback || isSavingDraft} onClick={() => handleRestoreDraft(draft.index)}>
                      Restore
                    </button>
                  </li>
                ))}
              </ul>
            ) : null}
          </section>
        </div>

        <div className="page-stack">
          <section className="card" aria-labelledby="feedback-detail-title">
            <div className="section-heading">
              <div>
                <p className="eyebrow">Submission Detail</p>
                <h2 id="feedback-detail-title">Analysis and final result</h2>
              </div>
            </div>

            {!submissionDetail ? <p className="muted">Select a submission to review analysis and feedback state.</p> : null}
            {submissionDetail ? (
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
                    <dt>Status</dt>
                    <dd>{submissionDetail.status}</dd>
                  </div>
                  <div>
                    <dt>Analysis</dt>
                    <dd>{submissionDetail.report ? 'Mock AI analysis available' : 'No Mock AI analysis report returned yet'}</dd>
                  </div>
                </dl>

                {submissionDetail.report ? (
                  <section className="analysis-report">
                    <h3>Mock AI Analysis</h3>
                    <p>{submissionDetail.report.summary}</p>
                    <p className="muted">
                      Mock Java sandbox/test runner results: {submissionDetail.report.testResults.length}
                    </p>
                  </section>
                ) : null}

                {finalResult ? (
                  <section className="analysis-report" aria-labelledby="final-feedback-title">
                    <h3 id="final-feedback-title">Final feedback sent</h3>
                    <p>{finalResult.finalFeedback}</p>
                    <p>
                      Grade: {finalResult.grade.points}/{finalResult.grade.maxPoints} · {finalResult.grade.explanation}
                    </p>
                    {finalResult.notification ? <p>Notification: {finalResult.notification.message}</p> : null}
                  </section>
                ) : null}
              </div>
            ) : null}
          </section>

          <TracePanel key={traceRefreshKey} title="Trace After Feedback" limit={5} />
        </div>
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
