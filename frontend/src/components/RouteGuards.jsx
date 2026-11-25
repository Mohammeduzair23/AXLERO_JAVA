import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/** Pages that need a signed-in user. Remembers where the person was heading. */
export function ProtectedRoute() {
  const { isAuthenticated } = useAuth();
  const location = useLocation();
  return isAuthenticated ? <Outlet /> : <Navigate to="/login" replace state={{ from: location }} />;
}

/** Login and signup: signed-in users are sent on to where they were going (or home). */
export function PublicOnlyRoute() {
  const { isAuthenticated } = useAuth();
  const location = useLocation();
  if (!isAuthenticated) return <Outlet />;
  return <Navigate to={location.state?.from?.pathname ?? '/'} replace />;
}