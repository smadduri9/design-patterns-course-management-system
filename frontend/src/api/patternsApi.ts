import { apiGet } from './client';
import type { PatternResponse } from './types';

export function getPatterns(): Promise<PatternResponse[]> {
  return apiGet<PatternResponse[]>('/api/app/patterns');
}
