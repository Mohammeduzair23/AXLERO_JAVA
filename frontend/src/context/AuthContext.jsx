import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { loginRequest, registerRequest, fetchCurrentUser } from '../api/authApi';
import { clearSession, readSession, saveSession, setUnauthorizedHandler } from '../api/client';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [session, setSession] = useState(() => readSession());

  const logout = useCallback(() => {
    clearSession();
    setSession(null);
  }, []);

  // An expired or revoked token anywhere in the app signs the user out.
  useEffect(() => {
    setUnauthorizedHandler(logout);
    return () => setUnauthorizedHandler(() => {});
  }, [logout]);

  // A stored token may have expired since the last visit: confirm it and refresh the user details.
  useEffect(() => {
    if (!readSession()) return;
    fetchCurrentUser()
      .then((user) => {
        const current = readSession();
        if (!current) return; // signed out while the request was in flight
        const refreshed = { ...current, user };
        saveSession(refreshed);
        setSession(refreshed);
      })
      .catch(() => {
        /* a 401 is handled by the interceptor; other failures can be ignored here */
      });
  }, []);

  const startSession = useCallback((data) => {
    const next = { token: data.token, user: data.user };
    saveSession(next);
    setSession(next);
  }, []);

  const login = useCallback(
    async (email, password) => startSession(await loginRequest(email, password)),
    [startSession],
  );

  const register = useCallback(
    async (details) => startSession(await registerRequest(details)),
    [startSession],
  );

  const value = useMemo(
    () => ({
      user: session?.user ?? null,
      isAuthenticated: Boolean(session?.token),
      login,
      register,
      logout,
    }),
    [session, login, register, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used inside <AuthProvider>');
  return context;
}