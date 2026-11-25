import { useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import AuthLayout from '../components/auth/AuthLayout';
import TextField from '../components/auth/TextField';
import { useAuth } from '../context/AuthContext';
import { parseApiError } from '../api/apiError';
import { focusFirstInvalid, validateSignup } from '../utils/validators';

export default function SignupPage() {
  const { register } = useAuth();
  const formRef = useRef(null);
  const [values, setValues] = useState({ fullName: '', email: '', password: '' });
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

    const found = validateSignup(values);
    if (Object.keys(found).length > 0) {
      setErrors(found);
      focusFirstInvalid(formRef.current);
      return;
    }

    setErrors({});
    setSubmitting(true);
    try {
      await register({
        fullName: values.fullName.trim(),
        email: values.email.trim(),
        password: values.password,
      });
      // On success the route guard moves the user on.
    } catch (error) {
      const parsed = parseApiError(error);
      if (parsed.status === 409) {
        setErrors({ email: 'An account with this email already exists. Sign in instead.' });
      } else {
        setErrors(parsed.fieldErrors);
        if (Object.keys(parsed.fieldErrors).length === 0) setFormError(parsed.message);
      }
      setSubmitting(false);
      focusFirstInvalid(formRef.current);
    }
  }

  return (
    <AuthLayout
      title="Create your account"
      subtitle="Set up your login, then create a workspace for your team."
      footer={
        <>
          Already have an account? <Link to="/login">Sign in</Link>
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
          label="Full name"
          name="fullName"
          autoComplete="name"
          value={values.fullName}
          onChange={handleChange}
          error={errors.fullName}
          maxLength={100}
          autoFocus
        />
        <TextField
          label="Work email"
          name="email"
          type="email"
          autoComplete="email"
          placeholder="name@company.com"
          value={values.email}
          onChange={handleChange}
          error={errors.email}
        />
        <TextField
          label="Password"
          name="password"
          type="password"
          revealable
          autoComplete="new-password"
          hint="Use 8 to 72 characters."
          value={values.password}
          onChange={handleChange}
          error={errors.password}
        />
        <button className="btn btn--primary" type="submit" disabled={submitting} aria-busy={submitting}>
          {submitting && <span className="btn__spinner" aria-hidden="true" />}
          {submitting ? 'Creating account…' : 'Create account'}
        </button>
      </form>
    </AuthLayout>
  );
}