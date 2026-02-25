import { useAuthStore } from "@/stores/authStore";

const BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

interface FetchOptions extends Omit<RequestInit, "body"> {
  body?: unknown;
}

let isRefreshing = false;
let refreshPromise: Promise<boolean> | null = null;

async function tryRefresh(): Promise<boolean> {
  if (isRefreshing && refreshPromise) return refreshPromise;

  isRefreshing = true;
  refreshPromise = (async () => {
    const { refreshToken, setTokens, clearTokens } =
      useAuthStore.getState();
    if (!refreshToken) {
      clearTokens();
      return false;
    }

    try {
      const res = await fetch(`${BASE_URL}/api/auth/refresh`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ refresh_token: refreshToken }),
      });

      if (!res.ok) {
        clearTokens();
        return false;
      }

      const data = await res.json();
      setTokens(data.access_token, data.refresh_token);
      return true;
    } catch {
      clearTokens();
      return false;
    } finally {
      isRefreshing = false;
      refreshPromise = null;
    }
  })();

  return refreshPromise;
}

export async function fetchApi<T>(
  path: string,
  options: FetchOptions = {}
): Promise<T> {
  const { accessToken } = useAuthStore.getState();

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options.headers as Record<string, string>),
  };

  if (accessToken) {
    headers["Authorization"] = `Bearer ${accessToken}`;
  }

  const res = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers,
    body: options.body ? JSON.stringify(options.body) : undefined,
  });

  if (res.status === 401 && accessToken) {
    const refreshed = await tryRefresh();
    if (refreshed) {
      const newToken = useAuthStore.getState().accessToken;
      headers["Authorization"] = `Bearer ${newToken}`;
      const retryRes = await fetch(`${BASE_URL}${path}`, {
        ...options,
        headers,
        body: options.body ? JSON.stringify(options.body) : undefined,
      });

      if (!retryRes.ok) {
        throw new ApiError(retryRes.status, await retryRes.text());
      }

      if (retryRes.status === 204) return undefined as T;
      return retryRes.json();
    }

    if (typeof window !== "undefined") {
      window.location.href = "/";
    }
    throw new ApiError(401, "Session expired");
  }

  if (!res.ok) {
    throw new ApiError(res.status, await res.text());
  }

  if (res.status === 204) return undefined as T;
  return res.json();
}

export class ApiError extends Error {
  constructor(
    public status: number,
    public body: string
  ) {
    super(`API Error ${status}: ${body}`);
  }
}
