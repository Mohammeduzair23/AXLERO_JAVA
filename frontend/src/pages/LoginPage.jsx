import { useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import AuthLayout from '../components/auth/AuthLayout';
import TextField from '../components/auth/TextField';
import { useAuth } from '../context/AuthContext';
import { parseApiError } from '../api/apiError';
import { focusFirstInvalid, validateLogin } from '../utils/validators';

export default function LoginPage() {
  const { login } = useAuth();
  const formRef = useRef(null);
  const [values, setValues] = useState({ email: '', password: '' });
  const [errors, setErrors] = useState({});
  const [formError, setFormError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  function handleChange(event) {
    const { name, value } = event.target;
    setValues((current) => ({ ...current, [name]: value }));
    if (errors[name]) setErrors((current) => ({ ...current, [name]: undefined }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    if (submitting) return;
    setFormError('');

    const found = validateLogin(values);
    if (Object.keys(found).length > 0) {
      setErrors(found);
      focusFirstInvalid(formRef.current);
      return;
    }

    setErrors({});
    setSubmitting(true);
    try {
      await login(values.email.trim(), values.password);
      // On success the route guard moves the user on, so there is nothing more to do here.
    } catch (error) {
      const parsed = parseApiError(error);
      setErrors(parsed.fieldErrors);
      setFormError(
        parsed.status === 401 ? 'The email or password is incorrect. Check them and try again.' : parsed.message,
      );
      setSubmitting(false);
      focusFirstInvalid(formRef.current);
    }
  }

  return (
    <AuthLayout
      title="Sign in"
      subtitle="Welcome back. Enter your details to open your workspaces."
      footer={
        <>
          New to Axlero? <Link to="/signup">Create an account</Link>
        </>
      }
    >
      <form ref={formRef} className="auth__form" onSubmit={handleSubmit} noValidate>
        {formError && (
          <div className="form-alert" role="alert">
            {formError}
          </div>
        )}
        <TextField
          label="Email"
          name="email"
          type="email"
          autoComplete="username"
          placeholder="name@company.com"
          value={values.email}
          onChange={handleChange}
          error={errors.email}
          autoFocus
        />
        <TextField
          label="Password"
          name="password"
          type="password"
          revealable
          autoComplete="current-password"
          value={values.password}
          onChange={handleChange}
          error={errors.password}
        />
        <button className="btn btn--primary" type="submit" disabled={submitting} aria-busy={submitting}>
          {submitting && <span className="btn__spinner" aria-hidden="true" />}
          {submitting ? 'Signing in…' : 'Sign in'}
        </button>
      </form>
    </AuthLayout>
  );
}