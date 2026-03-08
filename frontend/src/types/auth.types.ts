export interface AuthSession {
  accessToken: string;
  refreshToken: string;
  role: "ADMIN" | "DOCTOR" | "NURSE" | "RECEPTIONIST";
  name: string;
  email: string;
}

export interface LoginPayload {
  email: string;
  password: string;
}
