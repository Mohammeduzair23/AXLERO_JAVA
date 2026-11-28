import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import AppShell from './components/layout/AppShell';
import { ProtectedRoute, PublicOnlyRoute } from './components/RouteGuards';
import { AuthProvider } from './context/AuthContext';
import DashboardPage from './pages/DashboardPage';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import WorkspacePage from './pages/WorkspacePage';
import WorkspacesPage from './pages/WorkspacesPage';
import './styles/app.css';
import './styles/dashboards.css';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route element={<PublicOnlyRoute />}>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/signup" element={<SignupPage />} />
          </Route>
          <Route element={<ProtectedRoute />}>
            <Route element={<AppShell />}>
              <Route path="/" element={<WorkspacesPage />} />
              <Route path="/workspaces/:workspaceId" element={<WorkspacePage />} />
              <Route path="/workspaces/:workspaceId/dashboards/:dashboardId" element={<DashboardPage />} />
            </Route>
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}