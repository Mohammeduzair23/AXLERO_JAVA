/**
 * Turns any failed API call into { status, message, fieldErrors } using the backend's
 * standard error body: { status, message, fieldErrors: [{ field, message }] }.
 */
export function parseApiError(error) {
  if (!error.response) {
    return {
      status: 0,
      message:
        error.code === 'ECONNABORTED'
          ? 'The server took too long to respond. Try again.'
          : "Can't reach the server. Check that the backend is running, then try again.",
      fieldErrors: {},
    };
  }

  const { status, data } = error.response;
  const fieldErrors = {};
  (data?.fieldErrors ?? []).forEach(({ field, message }) => {
    if (!(field in fieldErrors)) fieldErrors[field] = message;
  });

  const message =
    status >= 500
      ? 'Something went wrong on our side. Try again in a moment.'
      : data?.message ?? 'Something went wrong. Try again.';

  return { status, message, fieldErrors };
}