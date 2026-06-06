import { apiGet, type QueryParams } from './client';
import type { TraceEventResponse } from './types';

export type TraceQuery = {
  category?: string;
  pattern?: string;
  workflowStep?: string;
  search?: string;
};

export function getTrace(query: TraceQuery = {}): Promise<TraceEventResponse[]> {
  return apiGet<TraceEventResponse[]>('/api/app/trace', query as QueryParams);
}
