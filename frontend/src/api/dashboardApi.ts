import { apiGet } from './client';
import type { DashboardResponse } from './types';

export function getDashboard(): Promise<DashboardResponse> {
  return apiGet<DashboardResponse>('/api/app/dashboard');
}
