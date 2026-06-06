export type QueryParams = Record<string, string | number | boolean | null | undefined>;

type BackendErrorBody = {
  status?: number;
  error?: string;
  message?: string;
  path?: string;
  timestamp?: string;
};

export class ApiError extends Error {
  readonly status: number;
  readonly path?: string;
  readonly details?: BackendErrorBody;

  constructor(status: number, message: string, path?: string, details?: BackendErrorBody) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.path = path;
    this.details = details;
  }
}

export function buildApiUrl(path: string, params?: QueryParams): string {
  const normalizedPath = path.startsWith('/') ? path : `/${path}`;
  const searchParams = new URLSearchParams();

  Object.entries(params ?? {}).forEach(([key, value]) => {
    if (value !== null && value !== undefined && value !== '') {
      searchParams.set(key, String(value));
    }
  });

  const query = searchParams.toString();
  return query ? `${normalizedPath}?${query}` : normalizedPath;
}

export async function apiGet<TResponse>(path: string, params?: QueryParams): Promise<TResponse> {
  return request<TResponse>(buildApiUrl(path, params), {
    method: 'GET',
  });
}

export async function apiPost<TRequest, TResponse>(
  path: string,
  body?: TRequest,
  params?: QueryParams,
): Promise<TResponse> {
  return request<TResponse>(buildApiUrl(path, params), {
    method: 'POST',
    headers: body === undefined ? undefined : { 'Content-Type': 'application/json' },
    body: body === undefined ? undefined : JSON.stringify(body),
  });
}

async function request<TResponse>(url: string, init: RequestInit): Promise<TResponse> {
  const response = await fetch(url, {
    ...init,
    headers: {
      Accept: 'application/json',
      ...init.headers,
    },
  });

  if (!response.ok) {
    throw await toApiError(response);
  }

  if (response.status === 204) {
    return undefined as TResponse;
  }

  return (await response.json()) as TResponse;
}

async function toApiError(response: Response): Promise<ApiError> {
  const body = await parseErrorBody(response);
  const message = body?.message || body?.error || `Request failed with status ${response.status}`;
  return new ApiError(response.status, message, body?.path, body);
}

async function parseErrorBody(response: Response): Promise<BackendErrorBody | undefined> {
  const contentType = response.headers.get('content-type') ?? '';
  if (!contentType.includes('application/json')) {
    return undefined;
  }

  try {
    return (await response.json()) as BackendErrorBody;
  } catch {
    return undefined;
  }
}
