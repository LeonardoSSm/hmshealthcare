export interface AuthSession {
  accessToken: string;
  refreshToken: string;
  role: UserRole;
  name: string;
  email: string;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export type UserRole = "ADMIN" | "DOCTOR" | "NURSE" | "RECEPTIONIST";
