/** Empty and error states: a title, one sentence, and optionally an action. */
export default function StateMessage({ title, text, tone = 'neutral', actionLabel, onAction, children }) {
  return (
    <div className={`state${tone === 'error' ? ' state--error' : ''}`} role={tone === 'error' ? 'alert' : undefined}>
      <h2 className="state__title">{title}</h2>
      {text && <p className="state__text">{text}</p>}
      {actionLabel && (
        <button type="button" className="btn btn--primary btn--inline" onClick={onAction}>
          {actionLabel}
        </button>
      )}
      {children}
    </div>
  );
}