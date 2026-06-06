import { apiGet, apiPost } from './client';
import type {
  CourseDetailResponse,
  CourseResponse,
  CreateCourseRequest,
  EnrollmentRequest,
  RosterResponse,
} from './types';

export function getCourses(): Promise<CourseResponse[]> {
  return apiGet<CourseResponse[]>('/api/app/courses');
}

export function createCourse(request: CreateCourseRequest): Promise<CourseResponse> {
  return apiPost<CreateCourseRequest, CourseResponse>('/api/app/courses', request);
}

export function getCourse(courseId: string): Promise<CourseDetailResponse> {
  return apiGet<CourseDetailResponse>(`/api/app/courses/${courseId}`);
}

export function getCourseRoster(courseId: string): Promise<RosterResponse> {
  return apiGet<RosterResponse>(`/api/app/courses/${courseId}/roster`);
}

export function enrollStudents(courseId: string, request: EnrollmentRequest): Promise<RosterResponse> {
  return apiPost<EnrollmentRequest, RosterResponse>(`/api/app/courses/${courseId}/enrollments`, request);
}
