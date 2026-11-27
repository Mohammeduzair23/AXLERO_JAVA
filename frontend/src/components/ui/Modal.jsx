import { useEffect, useId, useRef } from 'react';

/**
 * Accessible modal built on the native <dialog> element (focus is trapped and Esc closes it).
 * Children are only rendered while open, so forms start fresh every time.
 */
export default function Modal({ open, onClose, title, children }) {
  const dialogRef = useRef(null);
  const titleId = useId();

  useEffect(() => {
    const dialog = dialogRef.current;
    if (!dialog) return;
    if (open && !dialog.open) dialog.showModal();
    if (!open && dialog.open) dialog.close();
  }, [open]);

  return (
    <dialog
      ref={dialogRef}
      className="modal"
      aria-labelledby={titleId}
      onClose={onClose}
      onClick={(event) => {
        // A click on the dialog element itself (not its content) is a click on the backdrop.
        if (event.target === dialogRef.current) onClose();
      }}
    >
      {open && (
        <div className="modal__body">
          <h2 id={titleId} className="modal__title">
            {title}
          </h2>
          {children}
        </div>
      )}
    </dialog>
  );
}