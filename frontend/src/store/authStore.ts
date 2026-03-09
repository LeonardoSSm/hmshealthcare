import { create } from "zustand";
import type { AuthSession } from "../types/auth.types";

interface AuthState {
  session: AuthSession | null;
  setSession: (session: AuthSession) => void;
  clearSession: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  session: null,
  setSession: (session) => set({ session }),
  clearSession: () => set({ session: null })
}));
