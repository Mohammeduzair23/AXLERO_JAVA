import { useState } from 'react';
import Modal from '../ui/Modal';
import TextField from '../auth/TextField';
import TextAreaField from '../ui/TextAreaField';
import { parseApiError } from '../../api/apiError';
import { validateDashboard } from '../../utils/dashboardValidators';
import { focusFirstInvalid } from '../../utils/validators';

/** Used for both "New dashboard" and "Rename". `onSubmit({ name, description })` should reject on failure. */
export default function DashboardFormModal({ open, onClose, title, submitLabel, initialValues, onSubmit }) {
  return (
    <Modal open={open} onClose={onClose} title={title}>
      <DashboardForm initialValues={initialValues} submitLabel={submitLabel} onSubmit={onSubmit} onClose={onClose} />
    </Modal>
  );
}

function DashboardForm({ initialValues = { name: '', description: '' }, submitLabel, onSubmit, onClose }) {
  const [values, setValues] = useState({ name: initialValues.name, description: initialValues.description ?? '' });
  const [errors, setErrors] = useState({});
  const [formError, setFormError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [formElement, setFormElement] = useState(null);

  function handleChange(event) {
    const { name, value } = event.target;
    setValues((current) => ({ ...current, [name]: value }));
    if (errors[name]) setErrors((current) => ({ ...current, [name]: undefined }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    if (submitting) return;
    setFormError('');

    const found = validateDashboard(values);
    if (Object.keys(found).length > 0) {
      setErrors(found);
      focusFirstInvalid(formElement);
      return;
    }

    setErrors({});
    setSubmitting(true);
    try {
      await onSubmit({ name: values.name.trim(), description: values.description.trim() || null });
    } catch (failure) {
      const parsed = parseApiError(failure);
      setErrors(parsed.fieldErrors);
      if (Object.keys(parsed.fieldErrors).length === 0) setFormError(parsed.message);
      setSubmitting(false);
      focusFirstInvalid(formElement);
    }
  }

  return (
    <form ref={setFormElement} className="modal__form" onSubmit={handleSubmit} noValidate>
      {formError && (
        <div className="form-alert" role="alert">
          {formError}
        </div>
      )}
      <TextField
        label="Dashboard name"
        name="name"
        value={values.name}
        onChange={handleChange}
        error={errors.name}
        maxLength={150}
        autoFocus
      />
      <TextAreaField
        label="Description (optional)"
        name="description"
        value={values.description}
        onChange={handleChange}
        error={errors.description}
        hint="What this dashboard shows, in a sentence or two."
        rows={3}
      />
      <div className="modal__actions">
        <button type="button" className="btn btn--secondary btn--inline" onClick={onClose}>
          Cancel
        </button>
        <button type="submit" className="btn btn--primary btn--inline" disabled={submitting} aria-busy={submitting}>
          {submitting && <span className="btn__spinner" aria-hidden="true" />}
          {submitLabel}
        </button>
      </div>
    </form>
  );
}