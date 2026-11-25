// These mirror the backend rules (RegisterRequest / LoginRequest) so people get instant feedback.
// The backend still validates everything; this is only for speed and clarity.

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

function checkEmail(email) {
  const value = email.trim();
  if (!value) return 'Enter your email address.';
  if (!EMAIL_PATTERN.test(value)) return 'Enter a valid email address, like name@company.com.';
  return undefined;
}

export function validateLogin({ email, password }) {
  const errors = {};
  const emailError = checkEmail(email);
  if (emailError) errors.email = emailError;
  if (!password) errors.password = 'Enter your password.';
  return errors;
}

export function validateSignup({ fullName, email, password }) {
  const errors = {};
  if (!fullName.trim()) errors.fullName = 'Enter your full name.';
  else if (fullName.trim().length > 100) errors.fullName = 'Use 100 characters or fewer.';

  const emailError = checkEmail(email);
  if (emailError) errors.email = emailError;

  if (password.length < 8) errors.password = 'Use at least 8 characters.';
  else if (password.length > 72) errors.password = 'Use 72 characters or fewer.';
  return errors;
}

/** Moves keyboard focus to the first invalid field after a failed submit. */
export function focusFirstInvalid(form) {
  requestAnimationFrame(() => {
    form?.querySelector('[aria-invalid="true"]')?.focus();
  });
}