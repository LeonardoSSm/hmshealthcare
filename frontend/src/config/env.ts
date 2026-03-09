const defaultApiUrl = import.meta.env.DEV ? "http://localhost:8080/api" : "/api";

export const API_BASE_URL = import.meta.env.VITE_API_URL ?? defaultApiUrl;
