import { Navigate, Route, Routes } from "react-router-dom";
import LoginPage from "./pages/Auth/Login";
import DashboardPage from "./pages/Dashboard/DashboardPage";
import AttendanceQueuePage from "./pages/Queue/AttendanceQueuePage";
import QueuePanelPage from "./pages/Queue/QueuePanelPage";
import PatientListPage from "./pages/Patients/PatientList";
import MedicalRecordsPage from "./pages/Records/MedicalRecordsPage";
import AdmissionsPage from "./pages/Admissions/AdmissionsPage";
import BedsMapPage from "./pages/Beds/BedsMapPage";
import UsersPage from "./pages/Users/UsersPage";
import ProtectedRoute from "./components/ui/ProtectedRoute";
import { AppShell } from "./components/layout/AppShell";

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/queue/panel" element={<QueuePanelPage />} />
      <Route element={<ProtectedRoute />}>
        <Route element={<AppShell />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/queue" element={<AttendanceQueuePage />} />
          <Route path="/patients" element={<PatientListPage />} />
          <Route path="/records" element={<MedicalRecordsPage />} />
          <Route path="/admissions" element={<AdmissionsPage />} />
          <Route path="/beds" element={<BedsMapPage />} />
        </Route>
      </Route>

      <Route element={<ProtectedRoute roles={["ADMIN"]} />}>
        <Route element={<AppShell />}>
          <Route path="/users" element={<UsersPage />} />
        </Route>
      </Route>

      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}
