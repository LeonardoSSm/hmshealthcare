import axios from "axios";
import { useAuthStore } from "../store/authStore";
import { API_BASE_URL } from "../config/env";
import type { AuthSession } from "../types/auth.types";

export const api = axios.create({
  baseURL: API_BASE_URL
});

function setAuthorizationHeader(headers: any, token: string) {
  if (!headers) {
    return { Authorization: `Bearer ${token}` };
  }

  if (typeof headers.set === "function") {
    headers.set("Authorization", `Bearer ${token}`);
    return headers;
  }

  headers.Authorization = `Bearer ${token}`;
  return headers;
}

api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().session?.accessToken;
  if (token) {
    config.headers = setAuthorizationHeader(config.headers, token);
  }
  return config;
});

let isRefreshing = false;
let refreshPromise: Promise<AuthSession> | null = null;

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config as any;
    const status = error.response?.status;
    if (status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      const session = useAuthStore.getState().session;
      if (!session?.refreshToken) {
        useAuthStore.getState().clearSession();
        return Promise.reject(error);
      }

      if (isRefreshing && refreshPromise) {
        try {
          const refreshedSession = await refreshPromise;
          originalRequest.headers = setAuthorizationHeader(
            originalRequest.headers,
            refreshedSession.accessToken
          );
          return api(originalRequest);
        } catch (refreshError) {
          useAuthStore.getState().clearSession();
          return Promise.reject(refreshError);
        }
      }

      try {
        isRefreshing = true;
        refreshPromise =
          refreshPromise ??
          axios
            .post<AuthSession>(`${API_BASE_URL}/auth/refresh`, {
              refreshToken: session.refreshToken
            })
            .then((response) => response.data);

        const newSession = await refreshPromise;

        useAuthStore.getState().setSession(newSession);
        originalRequest.headers = setAuthorizationHeader(originalRequest.headers, newSession.accessToken);
        return api(originalRequest);
      } catch (refreshError) {
        useAuthStore.getState().clearSession();
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
        refreshPromise = null;
      }
    }

    return Promise.reject(error);
  }
);
