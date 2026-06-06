export type UserRole = 'INSTRUCTOR' | 'STUDENT';

export type PatternCategory = 'CREATIONAL' | 'STRUCTURAL' | 'BEHAVIORAL';

export type SubmissionType = 'PDF_TEXT' | 'JAVA_CODE';

export type GradingStrategyType = 'RUBRIC_WEIGHTED' | 'PASS_FAIL' | 'CODE_TEST';

export type SubmissionStatus =
  | 'DRAFT'
  | 'SUBMITTED'
  | 'ANALYZING'
  | 'AWAITING_REVIEW'
  | 'FINALIZED';

export type UserResponse = {
  id: string;
  name: string;
  role: UserRole;
};

export type DashboardCountsResponse = {
  courses: number;
  students: number;
  assignments: number;
  submissions: number;
  traceEvents: number;
};

export type DashboardResponse = {
  instructor: UserResponse;
  counts: DashboardCountsResponse;
};

export type PatternResponse = {
  key: string;
  displayName: string;
  category: PatternCategory;
};

export type TraceEventResponse = {
  timestamp: string;
  userAction: string;
  pattern: string;
  patternDisplayName: string;
  category: PatternCategory;
  className: string;
  description: string;
  workflowStep: string;
};

export type CourseResponse = {
  id: string;
  title: string;
  instructor: UserResponse;
  rosterCount: number;
  assignmentCount: number;
};

export type CourseDetailResponse = CourseResponse;

export type CreateCourseRequest = {
  title: string;
};

export type EnrollmentRequest = {
  studentIds: string[];
};

export type RosterResponse = {
  courseId: string;
  students: UserResponse[];
};

export type RubricCriterionRequest = {
  name: string;
  description: string;
  maxPoints: number;
};

export type RubricRequest = {
  title: string;
  criteria: RubricCriterionRequest[];
};

export type CreateAssignmentRequest = {
  title: string;
  description: string;
  dueDate: string;
  acceptedSubmissionTypes: SubmissionType[];
  gradingStrategyType: GradingStrategyType;
  maxPoints: number;
  rubric: RubricRequest;
};

export type RubricCriterionResponse = {
  id: string;
  name: string;
  description: string;
  maxPoints: number;
};

export type RubricResponse = {
  id: string;
  title: string;
  criteria: RubricCriterionResponse[];
};

export type AssignmentResponse = {
  id: string;
  courseId: string;
  title: string;
  description: string;
  dueDate: string;
  acceptedSubmissionTypes: SubmissionType[];
  gradingStrategyType: GradingStrategyType;
  maxPoints: number;
  rubric: RubricResponse;
};

export type CreateSubmissionRequest = {
  studentId: string;
  submissionType: SubmissionType;
  content: string;
};

export type GradeResponse = {
  points: number;
  maxPoints: number;
  explanation: string;
};

export type CriterionScoreResponse = {
  criterionId: string;
  pointsEarned: number;
  feedback: string;
};

export type TestResultResponse = {
  testName: string;
  passed: boolean;
  output: string;
};

export type AIAnalysisReportResponse = {
  id: string;
  summary: string;
  rubricFindings: CriterionScoreResponse[];
  testResults: TestResultResponse[];
  suggestedFeedback: string;
  gradeSuggestion: GradeResponse | null;
};

export type SubmissionListItemResponse = {
  id: string;
  assignmentId: string;
  student: UserResponse;
  type: SubmissionType;
  status: SubmissionStatus;
  submittedAt: string;
  hasAnalysisReport: boolean;
};

export type SubmissionDetailResponse = SubmissionListItemResponse & {
  report: AIAnalysisReportResponse | null;
};

export type AnalysisResponse = {
  submissionId: string;
  status: SubmissionStatus;
  report: AIAnalysisReportResponse | null;
};

export type FeedbackDraftSnapshotResponse = {
  index: number;
  feedbackText: string;
  savedAt: string;
};

export type FeedbackDraftResponse = {
  submissionId: string;
  currentFeedback: string;
  drafts: FeedbackDraftSnapshotResponse[];
};

export type SaveFeedbackDraftRequest = {
  feedbackText: string;
};

export type RestoreFeedbackDraftRequest = {
  draftIndex: number;
};

export type FinalizeFeedbackRequest = {
  feedbackText: string;
};

export type NotificationResponse = {
  id: string;
  message: string;
  createdAt: string;
  read: boolean;
};

export type FinalFeedbackResponse = {
  submissionId: string;
  status: SubmissionStatus;
  finalFeedback: string;
  grade: GradeResponse;
  notification: NotificationResponse;
};

export type StudentFeedbackResponse = {
  submissionId: string;
  finalFeedback: string;
  grade: GradeResponse;
  aiSummary: string;
  notification: NotificationResponse;
  report: AIAnalysisReportResponse;
};
