import { Navigate, Outlet } from "react-router-dom";
import { useAuthStore } from "../../store/authStore";

export default function ProtectedRoute() {
  const session = useAuthStore((state) => state.session);
  if (!session?.accessToken) {
    return <Navigate to="/login" replace />;
  }
  return <Outlet />;
}
