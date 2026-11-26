import BrandMark from '../BrandMark';
import RevenuePreview from './RevenuePreview';

/** Shared frame for login and signup: product story on the left, the form on the right. */
export default function AuthLayout({ title, subtitle, footer, children }) {
  return (
    <div className="auth">
      <aside className="auth__panel" aria-label="About Axlero Dashboards">
        <div className="auth__brand">
          <BrandMark />
          <span className="auth__brand-name">Axlero Dashboards</span>
        </div>
        <div className="auth__story">
          <p className="auth__headline">Turn sales data into dashboards your whole team can read.</p>
          <p className="auth__lede">
            Choose a metric, choose a chart, and see the numbers. Each workspace keeps its data private to its own
            members.
          </p>
          <RevenuePreview />
        </div>
      </aside>

      <main className="auth__main">
        <div className="auth__form-wrap">
          <h1 className="auth__title">{title}</h1>
          <p className="auth__subtitle">{subtitle}</p>
          {children}
          <p className="auth__switch">{footer}</p>
        </div>
      </main>
    </div>
  );
}