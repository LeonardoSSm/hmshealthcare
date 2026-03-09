import { Navigate, Outlet } from "react-router-dom";
import { useAuthStore } from "../../store/authStore";
import type { UserRole } from "../../types/auth.types";

interface ProtectedRouteProps {
  roles?: UserRole[];
}

export default function ProtectedRoute({ roles }: ProtectedRouteProps) {
  const session = useAuthStore((state) => state.session);

  if (!session?.accessToken) {
    return <Navigate to="/login" replace />;
  }

  if (roles && !roles.includes(session.role)) {
    return <Navigate to="/dashboard" replace />;
  }

  return <Outlet />;
}
