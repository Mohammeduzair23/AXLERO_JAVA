import axios from 'axios';

const STORAGE_KEY = 'axlero.session';

/*
 * The session ({ token, user }) lives in localStorage so a refresh keeps you signed in.
 * Trade-off: any script running on the page can read it, so keep third-party scripts out of this app.
 */
export function readSession() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

export function saveSession(session) {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
  } catch {
    /* storage unavailable (private mode): the session just won't survive a refresh */
  }
}

export function clearSession() {
  try {
    localStorage.removeItem(STORAGE_KEY);
  } catch {
    /* nothing to clear */
  }
}

let onUnauthorized = () => {};

/** AuthContext registers a callback here so an expired token signs the user out everywhere. */
export function setUnauthorizedHandler(handler) {
  onUnauthorized = handler;
}

const client = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? '',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
});

client.interceptors.request.use((config) => {
  const session = readSession();
  if (session?.token) {
    config.headers.Authorization = `Bearer ${session.token}`;
  }
  return config;
});

client.interceptors.response.use(
  (response) => response,
  (error) => {
    const url = error.config?.url ?? '';
    // A 401 from login/register means "wrong credentials", not "session expired".
    const isCredentialCall = url.startsWith('/api/auth/login') || url.startsWith('/api/auth/register');
    if (error.response?.status === 401 && !isCredentialCall) {
      onUnauthorized();
    }
    return Promise.reject(error);
  },
);

export default client;