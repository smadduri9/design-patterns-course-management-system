import { afterEach, describe, expect, it, vi } from 'vitest';

import { getTrace } from './traceApi';

describe('traceApi', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('calls unfiltered trace endpoint by default', async () => {
    const fetchMock = vi.fn().mockResolvedValue(jsonResponse([]));
    vi.stubGlobal('fetch', fetchMock);

    await getTrace();

    expect(fetchMock).toHaveBeenCalledWith('/api/app/trace', {
      method: 'GET',
      headers: { Accept: 'application/json' },
    });
  });

  it('sends trace filter query parameters', async () => {
    const fetchMock = vi.fn().mockResolvedValue(jsonResponse([]));
    vi.stubGlobal('fetch', fetchMock);

    await getTrace({
      category: 'STRUCTURAL',
      pattern: 'ADAPTER',
      workflowStep: 'Analyze',
      search: 'sandbox',
    });

    expect(fetchMock).toHaveBeenCalledWith(
      '/api/app/trace?category=STRUCTURAL&pattern=ADAPTER&workflowStep=Analyze&search=sandbox',
      {
        method: 'GET',
        headers: { Accept: 'application/json' },
      },
    );
  });
});

function jsonResponse(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  });
}
