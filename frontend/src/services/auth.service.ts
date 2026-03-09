import { api } from "./api";
import type { AuthSession, LoginPayload } from "../types/auth.types";

export async function login(payload: LoginPayload): Promise<AuthSession> {
  const response = await api.post<AuthSession>("/auth/login", payload);
  return response.data;
}

export async function refresh(refreshToken: string): Promise<AuthSession> {
  const response = await api.post<AuthSession>("/auth/refresh", { refreshToken });
  return response.data;
}

export async function logout(refreshToken: string): Promise<void> {
  await api.post("/auth/logout", { refreshToken });
}
