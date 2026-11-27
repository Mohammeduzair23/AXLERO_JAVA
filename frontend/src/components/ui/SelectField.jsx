import { useId } from 'react';

/** Label + select, same look and wiring as TextField. `options` is [{ value, label }]. */
export default function SelectField({ label, error, hint, options, ...selectProps }) {
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
      <select
        id={id}
        className={`field__input field__select${error ? ' is-invalid' : ''}`}
        aria-invalid={error ? 'true' : undefined}
        aria-describedby={message?.id}
        {...selectProps}
      >
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
      {message && (
        <p id={message.id} className={message.className}>
          {message.text}
        </p>
      )}
    </div>
  );
}