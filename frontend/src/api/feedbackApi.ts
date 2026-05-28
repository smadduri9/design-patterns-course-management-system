import { apiGet, apiPost } from './client';
import type {
  FeedbackDraftResponse,
  FinalFeedbackResponse,
  FinalizeFeedbackRequest,
  RestoreFeedbackDraftRequest,
  SaveFeedbackDraftRequest,
  StudentFeedbackResponse,
} from './types';

export function getFeedbackDrafts(submissionId: string): Promise<FeedbackDraftResponse> {
  return apiGet<FeedbackDraftResponse>(`/api/app/submissions/${submissionId}/feedback-drafts`);
}

export function saveFeedbackDraft(
  submissionId: string,
  request: SaveFeedbackDraftRequest,
): Promise<FeedbackDraftResponse> {
  return apiPost<SaveFeedbackDraftRequest, FeedbackDraftResponse>(
    `/api/app/submissions/${submissionId}/feedback-drafts`,
    request,
  );
}

export function restoreFeedbackDraft(
  submissionId: string,
  request: RestoreFeedbackDraftRequest,
): Promise<FeedbackDraftResponse> {
  return apiPost<RestoreFeedbackDraftRequest, FeedbackDraftResponse>(
    `/api/app/submissions/${submissionId}/feedback-drafts/restore`,
    request,
  );
}

export function finalizeFeedback(
  submissionId: string,
  request: FinalizeFeedbackRequest,
): Promise<FinalFeedbackResponse> {
  return apiPost<FinalizeFeedbackRequest, FinalFeedbackResponse>(
    `/api/app/submissions/${submissionId}/final-feedback`,
    request,
  );
}

export function getStudentFeedback(submissionId: string): Promise<StudentFeedbackResponse> {
  return apiGet<StudentFeedbackResponse>(`/api/app/submissions/${submissionId}/student-feedback`);
}
