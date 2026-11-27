import { useState } from 'react';
import Modal from '../ui/Modal';
import TextField from '../auth/TextField';
import { parseApiError } from '../../api/apiError';
import { validateWorkspaceName } from '../../utils/workspaceValidators';

/** Used for both "New workspace" and "Rename". `onSubmit(name)` should reject on failure. */
export default function WorkspaceNameModal({ open, onClose, title, submitLabel, initialName = '', onSubmit }) {
  return (
    <Modal open={open} onClose={onClose} title={title}>
      <NameForm initialName={initialName} submitLabel={submitLabel} onSubmit={onSubmit} onClose={onClose} />
    </Modal>
  );
}

function NameForm({ initialName, submitLabel, onSubmit, onClose }) {
  const [name, setName] = useState(initialName);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();
    if (submitting) return;
    const problem = validateWorkspaceName(name);
    if (problem) {
      setError(problem);
      return;
    }
    setError('');
    setSubmitting(true);
    try {
      await onSubmit(name.trim());
    } catch (failure) {
      const parsed = parseApiError(failure);
      setError(parsed.fieldErrors.name ?? parsed.message);
      setSubmitting(false);
    }
  }

  return (
    <form className="modal__form" onSubmit={handleSubmit} noValidate>
      <TextField
        label="Workspace name"
        name="name"
        value={name}
        onChange={(event) => {
          setName(event.target.value);
          if (error) setError('');
        }}
        error={error}
        hint="For example, the name of your team or company."
        maxLength={100}
        autoFocus
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