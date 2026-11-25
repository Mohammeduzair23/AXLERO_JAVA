// The same numbers the API returns for "SUM of amount by region" on the sample sales data.
const ROWS = [
  { label: 'East', value: 180 },
  { label: 'North', value: 150 },
  { label: 'South', value: 275.5 },
];
const MAX = Math.max(...ROWS.map((row) => row.value));
const SUMMARY = `Example bar chart of revenue by region: ${ROWS.map((r) => `${r.label} ${r.value}`).join(', ')}.`;

/** A small, real-looking widget so people see what they are about to build. */
export default function RevenuePreview() {
  return (
    <figure className="preview" role="img" aria-label={SUMMARY}>
      <figcaption className="preview__head">
        <span className="preview__title">Revenue by region</span>
        <span className="preview__sub">Acme Sales</span>
      </figcaption>
      <ul className="preview__bars">
        {ROWS.map((row, index) => (
          <li key={row.label} className="preview__row">
            <span className="preview__label">{row.label}</span>
            <span className="preview__track">
              <span
                className={`preview__bar${row.value === MAX ? ' is-top' : ''}`}
                style={{ '--w': `${(row.value / MAX) * 100}%`, '--i': index }}
              />
            </span>
            <span className="preview__value">{row.value.toLocaleString('en-IN')}</span>
          </li>
        ))}
      </ul>
      <p className="preview__foot">Built from 6 sales records</p>
    </figure>
  );
}