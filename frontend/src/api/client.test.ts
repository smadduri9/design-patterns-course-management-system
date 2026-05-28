import { afterEach, describe, expect, it, vi } from 'vitest';

import { ApiError, apiGet, apiPost, buildApiUrl } from './client';

describe('API client', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('builds relative API URLs with query parameters', () => {
    expect(
      buildApiUrl('/api/app/trace', {
        category: 'STRUCTURAL',
        pattern: '',
        search: 'adapter',
        workflowStep: undefined,
      }),
    ).toBe('/api/app/trace?category=STRUCTURAL&search=adapter');
  });

  it('uses fetch for typed GET requests', async () => {
    const fetchMock = vi.fn().mockResolvedValue(
      new Response(JSON.stringify({ ok: true }), {
        status: 200,
        headers: { 'Content-Type': 'application/json' },
      }),
    );
    vi.stubGlobal('fetch', fetchMock);

    const result = await apiGet<{ ok: boolean }>('/api/app/dashboard');

    expect(result).toEqual({ ok: true });
    expect(fetchMock).toHaveBeenCalledWith('/api/app/dashboard', {
      method: 'GET',
      headers: {
        Accept: 'application/json',
      },
    });
  });

  it('serializes POST request bodies as JSON', async () => {
    const fetchMock = vi.fn().mockResolvedValue(
      new Response(JSON.stringify({ id: 'course-id' }), {
        status: 200,
        headers: { 'Content-Type': 'application/json' },
      }),
    );
    vi.stubGlobal('fetch', fetchMock);

    await apiPost<{ title: string }, { id: string }>('/api/app/courses', { title: 'Design Patterns' });

    expect(fetchMock).toHaveBeenCalledWith('/api/app/courses', {
      method: 'POST',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ title: 'Design Patterns' }),
    });
  });

  it('throws ApiError for backend error responses', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(
        new Response(
          JSON.stringify({
            status: 404,
            error: 'Not Found',
            message: 'course was not found',
            path: '/api/app/courses/missing',
          }),
          {
            status: 404,
            headers: { 'Content-Type': 'application/json' },
          },
        ),
      ),
    );

    await expect(apiGet('/api/app/courses/missing')).rejects.toMatchObject({
      name: 'ApiError',
      status: 404,
      message: 'course was not found',
      path: '/api/app/courses/missing',
    });
  });
});
