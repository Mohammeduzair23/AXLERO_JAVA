import { useState } from 'react';
import Modal from './Modal';
import { parseApiError } from '../../api/apiError';

/** A "this can't be undone" confirmation. `onConfirm` should reject if the delete fails. */
export default function ConfirmDeleteModal({ open, onClose, title, text, confirmLabel, onConfirm }) {
  return (
    <Modal open={open} onClose={onClose} title={title}>
      <ConfirmBody text={text} confirmLabel={confirmLabel} onConfirm={onConfirm} onClose={onClose} />
    </Modal>
  );
}

function ConfirmBody({ text, confirmLabel, onConfirm, onClose }) {
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
      <p className="modal__text">{text}</p>
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
          {confirmLabel}
        </button>
      </div>
    </>
  );
}