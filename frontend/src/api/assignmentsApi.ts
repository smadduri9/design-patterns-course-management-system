import { apiGet, apiPost } from './client';
import type { AssignmentResponse, CreateAssignmentRequest } from './types';

export function getCourseAssignments(courseId: string): Promise<AssignmentResponse[]> {
  return apiGet<AssignmentResponse[]>(`/api/app/courses/${courseId}/assignments`);
}

export function createAssignment(
  courseId: string,
  request: CreateAssignmentRequest,
): Promise<AssignmentResponse> {
  return apiPost<CreateAssignmentRequest, AssignmentResponse>(
    `/api/app/courses/${courseId}/assignments`,
    request,
  );
}

export function getAssignment(assignmentId: string): Promise<AssignmentResponse> {
  return apiGet<AssignmentResponse>(`/api/app/assignments/${assignmentId}`);
}
