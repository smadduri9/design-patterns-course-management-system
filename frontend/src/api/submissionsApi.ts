import { apiGet, apiPost } from './client';
import type {
  AnalysisResponse,
  CreateSubmissionRequest,
  SubmissionDetailResponse,
  SubmissionListItemResponse,
} from './types';

export function getAssignmentSubmissions(assignmentId: string): Promise<SubmissionListItemResponse[]> {
  return apiGet<SubmissionListItemResponse[]>(`/api/app/assignments/${assignmentId}/submissions`);
}

export function createSubmission(
  assignmentId: string,
  request: CreateSubmissionRequest,
): Promise<SubmissionDetailResponse> {
  return apiPost<CreateSubmissionRequest, SubmissionDetailResponse>(
    `/api/app/assignments/${assignmentId}/submissions`,
    request,
  );
}

export function getSubmission(submissionId: string): Promise<SubmissionDetailResponse> {
  return apiGet<SubmissionDetailResponse>(`/api/app/submissions/${submissionId}`);
}

export function analyzeSubmission(submissionId: string): Promise<AnalysisResponse> {
  return apiPost<undefined, AnalysisResponse>(`/api/app/submissions/${submissionId}/analyze`, undefined);
}
