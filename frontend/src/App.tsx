import { Navigate, Route, Routes } from "react-router-dom";
import LoginPage from "./pages/Auth/Login";
import PatientListPage from "./pages/Patients/PatientList";
import ProtectedRoute from "./components/ui/ProtectedRoute";

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route element={<ProtectedRoute />}>
        <Route path="/patients" element={<PatientListPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/patients" replace />} />
    </Routes>
  );
}
