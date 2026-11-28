/** Mirrors DashboardRequest: name required (max 150), description optional (max 500). */
export function validateDashboard({ name, description }) {
  const errors = {};
  const trimmedName = name.trim();
  if (!trimmedName) errors.name = 'Enter a dashboard name.';
  else if (trimmedName.length > 150) errors.name = 'Use 150 characters or fewer.';
  if (description.trim().length > 500) errors.description = 'Use 500 characters or fewer.';
  return errors;
}