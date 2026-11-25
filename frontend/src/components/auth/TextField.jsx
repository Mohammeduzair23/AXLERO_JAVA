import { useId, useState } from 'react';

/** Label + input + message, wired together for screen readers. `revealable` adds a show/hide button. */
export default function TextField({ label, error, hint, revealable = false, type = 'text', ...inputProps }) {
  const id = useId();
  const [shown, setShown] = useState(false);

  const message = error ? { id: `${id}-error`, text: error, className: 'field__error' } : hint ? { id: `${id}-hint`, text: hint, className: 'field__hint' } : null;

  return (
    <div className="field">
      <label htmlFor={id} className="field__label">
        {label}
      </label>
      <div className={`field__control${revealable ? ' field__control--revealable' : ''}`}>
        <input
          id={id}
          className={`field__input${error ? ' is-invalid' : ''}`}
          type={revealable && shown ? 'text' : type}
          aria-invalid={error ? 'true' : undefined}
          aria-describedby={message?.id}
          {...inputProps}
        />
        {revealable && (
          <button
            type="button"
            className="field__reveal"
            onClick={() => setShown((value) => !value)}
            aria-pressed={shown}
            aria-label={shown ? 'Hide password' : 'Show password'}
          >
            {shown ? 'Hide' : 'Show'}
          </button>
        )}
      </div>
      {message && (
        <p id={message.id} className={message.className}>
          {message.text}
        </p>
      )}
    </div>
  );
}