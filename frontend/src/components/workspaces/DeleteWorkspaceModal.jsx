import { useState } from 'react';
import Modal from '../ui/Modal';
import { parseApiError } from '../../api/apiError';

export default function DeleteWorkspaceModal({ open, onClose, workspaceName, onConfirm }) {
  return (
    <Modal open={open} onClose={onClose} title="Delete this workspace?">
      <ConfirmBody workspaceName={workspaceName} onConfirm={onConfirm} onClose={onClose} />
    </Modal>
  );
}

function ConfirmBody({ workspaceName, onConfirm, onClose }) {
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  async function handleConfirm() {
    if (submitting) return;
    setError('');
    setSubmitting(true);
    try {
      await onConfirm();
    } catch (failure) {
      setError(parseApiError(failure).message);
      setSubmitting(false);
    }
  }

  return (
    <>
      <p className="modal__text">
        “{workspaceName}” will be removed permanently, together with its members, dashboards and sales data. This can’t
        be undone.
      </p>
      {error && (
        <div className="form-alert modal__alert" role="alert">
          {error}
        </div>
      )}
      <div className="modal__actions">
        <button type="button" className="btn btn--secondary btn--inline" onClick={onClose}>
          Cancel
        </button>
        <button
          type="button"
          className="btn btn--danger btn--inline"
          onClick={handleConfirm}
          disabled={submitting}
          aria-busy={submitting}
        >
          {submitting && <span className="btn__spinner" aria-hidden="true" />}
          Delete workspace
        </button>
      </div>
    </>
  );
}