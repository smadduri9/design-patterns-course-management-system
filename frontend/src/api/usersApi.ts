import { apiGet } from './client';
import type { UserResponse } from './types';

export function getStudents(): Promise<UserResponse[]> {
  return apiGet<UserResponse[]>('/api/app/students');
}
