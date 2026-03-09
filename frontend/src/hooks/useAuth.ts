import { useMutation } from "@tanstack/react-query";
import { useQueryClient } from "@tanstack/react-query";
import { useAuthStore } from "../store/authStore";
import { login as loginRequest, logout as logoutRequest } from "../services/auth.service";

export function useAuth() {
  const queryClient = useQueryClient();
  const setSession = useAuthStore((s) => s.setSession);
  const clearSession = useAuthStore((s) => s.clearSession);
  const session = useAuthStore((s) => s.session);

  const login = useMutation({
    mutationFn: loginRequest,
    onSuccess: (data) => setSession(data)
  });

  const logout = useMutation({
    mutationFn: async () => {
      if (session?.refreshToken) {
        await logoutRequest(session.refreshToken);
      }
    },
    onSettled: () => {
      clearSession();
      queryClient.clear();
    }
  });

  return { session, login, logout };
}
