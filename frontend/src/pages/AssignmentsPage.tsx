import { FormEvent, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import { createAssignment, getCourseAssignments } from '../api/assignmentsApi';
import { getCourses } from '../api/coursesApi';
import type {
  AssignmentResponse,
  CourseResponse,
  CreateAssignmentRequest,
  GradingStrategyType,
  RubricCriterionRequest,
  SubmissionType,
} from '../api/types';

const todayIsoDate = new Date().toISOString().slice(0, 10);

const emptyCriterion = (): RubricCriterionRequest => ({
  name: '',
  description: '',
  maxPoints: 100,
});

export function AssignmentsPage() {
  const [courses, setCourses] = useState<CourseResponse[]>([]);
  const [selectedCourseId, setSelectedCourseId] = useState('');
  const [assignments, setAssignments] = useState<AssignmentResponse[]>([]);
  const [isLoadingCourses, setIsLoadingCourses] = useState(true);
  const [isLoadingAssignments, setIsLoadingAssignments] = useState(false);
  const [isCreating, setIsCreating] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [dueDate, setDueDate] = useState(todayIsoDate);
  const [submissionType, setSubmissionType] = useState<SubmissionType>('PDF_TEXT');
  const [gradingStrategyType, setGradingStrategyType] = useState<GradingStrategyType>('RUBRIC_WEIGHTED');
  const [maxPoints, setMaxPoints] = useState(100);
  const [rubricTitle, setRubricTitle] = useState('Assignment Rubric');
  const [criteria, setCriteria] = useState<RubricCriterionRequest[]>([emptyCriterion()]);

  useEffect(() => {
    void loadCourses();
  }, []);

  useEffect(() => {
    if (!selectedCourseId) {
      setAssignments([]);
      return;
    }

    void loadAssignments(selectedCourseId);
  }, [selectedCourseId]);

  async function loadCourses(nextSelectedCourseId?: string) {
    setIsLoadingCourses(true);
    try {
      const courseResponse = await getCourses();
      setCourses(courseResponse);
      setError(null);
      if (nextSelectedCourseId) {
        setSelectedCourseId(nextSelectedCourseId);
      } else if (!selectedCourseId && courseResponse.length > 0) {
        setSelectedCourseId(courseResponse[0].id);
      }
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
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load assignments');
    } finally {
      setIsLoadingAssignments(false);
    }
  }

  async function handleCreateAssignment(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!selectedCourseId) {
      setError('Select a course before creating an assignment');
      return;
    }

    const request = buildCreateAssignmentRequest();
    if (!request) {
      return;
    }

    setIsCreating(true);
    try {
      await createAssignment(selectedCourseId, request);
      resetForm();
      await loadAssignments(selectedCourseId);
      await loadCourses(selectedCourseId);
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to create assignment');
    } finally {
      setIsCreating(false);
    }
  }

  function buildCreateAssignmentRequest(): CreateAssignmentRequest | null {
    const cleanedCriteria = criteria.map((criterion) => ({
      name: criterion.name.trim(),
      description: criterion.description.trim(),
      maxPoints: Number(criterion.maxPoints),
    }));

    if (!title.trim() || !description.trim() || !dueDate || !rubricTitle.trim()) {
      setError('Assignment title, description, due date, and rubric title are required');
      return null;
    }
    if (maxPoints <= 0 || cleanedCriteria.some((criterion) => criterion.maxPoints <= 0)) {
      setError('Max points must be positive');
      return null;
    }
    if (cleanedCriteria.some((criterion) => !criterion.name || !criterion.description)) {
      setError('Each rubric criterion needs a name and description');
      return null;
    }

    return {
      title: title.trim(),
      description: description.trim(),
      dueDate,
      acceptedSubmissionTypes: [submissionType],
      gradingStrategyType,
      maxPoints: Number(maxPoints),
      rubric: {
        title: rubricTitle.trim(),
        criteria: cleanedCriteria,
      },
    };
  }

  function resetForm() {
    setTitle('');
    setDescription('');
    setDueDate(todayIsoDate);
    setSubmissionType('PDF_TEXT');
    setGradingStrategyType('RUBRIC_WEIGHTED');
    setMaxPoints(100);
    setRubricTitle('Assignment Rubric');
    setCriteria([emptyCriterion()]);
  }

  function updateCriterion(index: number, update: Partial<RubricCriterionRequest>) {
    setCriteria((current) =>
      current.map((criterion, currentIndex) =>
        currentIndex === index ? { ...criterion, ...update } : criterion,
      ),
    );
  }

  function removeCriterion(index: number) {
    setCriteria((current) => (current.length === 1 ? current : current.filter((_, currentIndex) => currentIndex !== index)));
  }

  return (
    <section className="page-stack" aria-labelledby="assignments-title">
      <div className="hero-card hero-card--compact page-hero">
        <div>
          <p className="eyebrow">Assignments</p>
          <h2 id="assignments-title">Assignment and rubric builder</h2>
        </div>
        <p>
          Create assignments for a course and define the rubric students will be graded against.
        </p>
      </div>

      {error ? <section className="card error-text">{error}</section> : null}

      <section className="assignments-layout">
        <div className="page-stack">
          <section className="card" aria-labelledby="assignment-list-title">
            <div className="section-heading">
              <div>
                <p className="eyebrow">Course Assignments</p>
                <h2 id="assignment-list-title">Assignments</h2>
              </div>
              <span className="badge">{assignments.length} assignments</span>
            </div>

            <label className="field">
              Course
              <select
                value={selectedCourseId}
                onChange={(event) => setSelectedCourseId(event.target.value)}
                disabled={isLoadingCourses}
              >
                {courses.length === 0 ? <option value="">No courses yet</option> : null}
                {courses.map((course) => (
                  <option value={course.id} key={course.id}>
                    {course.title}
                  </option>
                ))}
              </select>
            </label>

            {isLoadingCourses || isLoadingAssignments ? <p className="muted">Loading assignments...</p> : null}
            {!isLoadingCourses && selectedCourseId && !isLoadingAssignments && assignments.length === 0 ? (
              <p className="muted">No assignments from the backend for this course yet.</p>
            ) : null}
            {!isLoadingCourses && !selectedCourseId ? (
              <p className="muted">Create a course before adding assignments.</p>
            ) : null}
            {assignments.length > 0 ? (
              <div className="assignment-list" aria-label="Assignments">
                {assignments.map((assignment) => (
                  <article className="assignment-card" key={assignment.id}>
                    <div className="entity-card__header">
                      <span className="card-icon card-icon--accent" aria-hidden="true">AS</span>
                      <div>
                        <h3>{assignment.title}</h3>
                        <p>{assignment.description}</p>
                      </div>
                      <span className="badge badge--accent">Type: {assignment.acceptedSubmissionTypes.join(', ')}</span>
                    </div>
                    <dl>
                      <div>
                        <dt>Submission</dt>
                        <dd>{assignment.acceptedSubmissionTypes.join(', ')}</dd>
                      </div>
                      <div>
                        <dt>Max points</dt>
                        <dd>{assignment.maxPoints}</dd>
                      </div>
                      <div>
                        <dt>Rubric</dt>
                        <dd>
                          {assignment.rubric.title} · {assignment.rubric.criteria.length} criteria
                        </dd>
                      </div>
                      <div>
                        <dt>ID</dt>
                        <dd>{assignment.id}</dd>
                      </div>
                    </dl>
                    <Link to="/submissions">Manage submissions</Link>
                  </article>
                ))}
              </div>
            ) : null}
          </section>
        </div>

        <section className="card form-card" aria-labelledby="create-assignment-title">
          <div className="section-heading">
            <div>
              <p className="eyebrow">Create Assignment</p>
              <h2 id="create-assignment-title">New assignment</h2>
            </div>
          </div>

          <form className="assignment-form" onSubmit={handleCreateAssignment}>
            <label className="field">
              Course
              <select value={selectedCourseId} onChange={(event) => setSelectedCourseId(event.target.value)}>
                {courses.length === 0 ? <option value="">No courses yet</option> : null}
                {courses.map((course) => (
                  <option value={course.id} key={course.id}>
                    {course.title}
                  </option>
                ))}
              </select>
            </label>

            <label className="field">
              Assignment title
              <input value={title} onChange={(event) => setTitle(event.target.value)} placeholder="Adapter Pattern Essay" />
            </label>

            <label className="field">
              Description
              <textarea
                value={description}
                onChange={(event) => setDescription(event.target.value)}
                placeholder="Explain how Adapter protects the domain from external services."
              />
            </label>

            <div className="form-grid">
              <label className="field">
                Due date
                <input type="date" value={dueDate} onChange={(event) => setDueDate(event.target.value)} />
              </label>
              <label className="field">
                Max points
                <input type="number" min="1" value={maxPoints} onChange={(event) => setMaxPoints(Number(event.target.value))} />
              </label>
            </div>

            <div className="form-grid">
              <label className="field">
                Accepted submission type
                <select value={submissionType} onChange={(event) => setSubmissionType(event.target.value as SubmissionType)}>
                  <option value="PDF_TEXT">PDF/Text</option>
                  <option value="JAVA_CODE">Java Code</option>
                </select>
              </label>
              <label className="field">
                Grading strategy
                <select
                  value={gradingStrategyType}
                  onChange={(event) => setGradingStrategyType(event.target.value as GradingStrategyType)}
                >
                  <option value="RUBRIC_WEIGHTED">Rubric Weighted</option>
                  <option value="PASS_FAIL">Pass/Fail</option>
                  <option value="CODE_TEST">Code Test</option>
                </select>
              </label>
            </div>

            <label className="field">
              Rubric title
              <input value={rubricTitle} onChange={(event) => setRubricTitle(event.target.value)} />
            </label>

            <div className="criteria-editor">
              <div className="section-heading">
                <h3>Rubric criteria</h3>
                <button type="button" onClick={() => setCriteria((current) => [...current, emptyCriterion()])}>
                  Add criterion
                </button>
              </div>
              {criteria.map((criterion, index) => (
                <fieldset className="criterion-card" key={index}>
                  <legend>Criterion {index + 1}</legend>
                  <label className="field">
                    Name
                    <input
                      value={criterion.name}
                      onChange={(event) => updateCriterion(index, { name: event.target.value })}
                      placeholder="Correctness"
                    />
                  </label>
                  <label className="field">
                    Description
                    <textarea
                      value={criterion.description}
                      onChange={(event) => updateCriterion(index, { description: event.target.value })}
                      placeholder="Accurate use of pattern concepts."
                    />
                  </label>
                  <label className="field">
                    Max points
                    <input
                      type="number"
                      min="1"
                      value={criterion.maxPoints}
                      onChange={(event) => updateCriterion(index, { maxPoints: Number(event.target.value) })}
                    />
                  </label>
                  <button type="button" onClick={() => removeCriterion(index)} disabled={criteria.length === 1}>
                    Remove criterion
                  </button>
                </fieldset>
              ))}
            </div>

            <button type="submit" disabled={isCreating || !selectedCourseId}>
              {isCreating ? 'Creating assignment...' : 'Create assignment'}
            </button>
          </form>
        </section>
      </section>
    </section>
  );
}
