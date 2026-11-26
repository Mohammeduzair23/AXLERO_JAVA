/** Two overlapping squares, taken from the Axlero logo. */
export default function BrandMark({ size = 28 }) {
  return (
    <svg width={size} height={size} viewBox="0 0 32 32" aria-hidden="true" focusable="false">
      <rect x="2" y="9" width="17" height="17" rx="3" fill="#5AB1F0" />
      <rect x="13" y="3" width="17" height="17" rx="3" fill="#8F78E0" />
      <rect x="13" y="9" width="6" height="11" fill="#3E4FA8" />
    </svg>
  );
}