import { api } from "./api";
import type { UserRole } from "../types/auth.types";

export interface UserRemoteResponse {
  id: string;
  name: string;
  email: string;
  role: UserRole;
  active: boolean;
  createdAt: string;
}

export interface CreateUserRemotePayload {
  name: string;
  email: string;
  password: string;
  role: UserRole;
  active: boolean;
}

export interface UpdateUserRemotePayload {
  name: string;
  email: string;
  password?: string;
  role: UserRole;
  active: boolean;
}

export async function listUsers(): Promise<UserRemoteResponse[]> {
  const response = await api.get<UserRemoteResponse[]>("/users");
  return response.data;
}

export async function listDoctors(): Promise<UserRemoteResponse[]> {
  const response = await api.get<UserRemoteResponse[]>("/users/doctors");
  return response.data;
}

export async function listNurses(): Promise<UserRemoteResponse[]> {
  const response = await api.get<UserRemoteResponse[]>("/users/nurses");
  return response.data;
}

export async function createUser(payload: CreateUserRemotePayload): Promise<UserRemoteResponse> {
  const response = await api.post<UserRemoteResponse>("/users", payload);
  return response.data;
}

export async function updateUser(id: string, payload: UpdateUserRemotePayload): Promise<UserRemoteResponse> {
  const response = await api.put<UserRemoteResponse>(`/users/${id}`, payload);
  return response.data;
}
