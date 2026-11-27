import { useCallback, useEffect, useState } from 'react';
import { parseApiError } from '../api/apiError';

/**
 * Runs an async function when the component mounts (and whenever `deps` change) and tracks
 * { data, error, loading }. `reload()` fetches again; the previous data stays visible meanwhile.
 * `error` is the parsed API error: { status, message, fieldErrors }.
 */
export default function useAsync(asyncFn, deps = []) {
  const [state, setState] = useState({ data: null, error: null, loading: true });
  const [version, setVersion] = useState(0);

  useEffect(() => {
    let cancelled = false;
    setState((current) => ({ ...current, loading: true, error: null }));
    asyncFn().then(
      (data) => {
        if (!cancelled) setState({ data, error: null, loading: false });
      },
      (error) => {
        if (!cancelled) setState({ data: null, error: parseApiError(error), loading: false });
      },
    );
    return () => {
      cancelled = true;
    };
    // The caller decides when to refetch through `deps`; asyncFn itself is recreated every render.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [...deps, version]);

  const reload = useCallback(() => setVersion((value) => value + 1), []);
  return { ...state, reload };
}