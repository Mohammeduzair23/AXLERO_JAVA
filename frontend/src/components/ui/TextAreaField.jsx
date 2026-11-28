import { useId } from 'react';

/** Label + textarea, same look and wiring as TextField. */
export default function TextAreaField({ label, error, hint, ...textareaProps }) {
  const id = useId();
  const message = error
    ? { id: `${id}-error`, text: error, className: 'field__error' }
    : hint
      ? { id: `${id}-hint`, text: hint, className: 'field__hint' }
      : null;

  return (
    <div className="field">
      <label htmlFor={id} className="field__label">
        {label}
      </label>
      <textarea
        id={id}
        className={`field__input field__textarea${error ? ' is-invalid' : ''}`}
        aria-invalid={error ? 'true' : undefined}
        aria-describedby={message?.id}
        {...textareaProps}
      />
      {message && (
        <p id={message.id} className={message.className}>
          {message.text}
        </p>
      )}
    </div>
  );
}