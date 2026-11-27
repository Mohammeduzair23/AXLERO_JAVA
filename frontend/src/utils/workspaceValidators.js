const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export function validateWorkspaceName(name) {
  const value = name.trim();
  if (!value) return 'Enter a workspace name.';
  if (value.length > 100) return 'Use 100 characters or fewer.';
  return undefined;
}

export function validateInvite({ email }) {
  const errors = {};
  const value = email.trim();
  if (!value) errors.email = 'Enter the email address of the person to add.';
  else if (!EMAIL_PATTERN.test(value)) errors.email = 'Enter a valid email address, like name@company.com.';
  return errors;
}