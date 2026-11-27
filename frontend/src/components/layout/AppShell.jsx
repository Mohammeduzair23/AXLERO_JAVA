import { Link, Outlet } from 'react-router-dom';
import BrandMark from '../BrandMark';
import UserMenu from './UserMenu';

/** Frame for every signed-in page: top bar with the user menu, then the page content. */
export default function AppShell() {
  return (
    <div className="shell">
      <a className="skip-link" href="#main">
        Skip to content
      </a>
      <header className="shell__bar">
        <div className="shell__bar-inner">
          <Link to="/" className="shell__brand">
            <BrandMark size={24} />
            <span>Axlero Dashboards</span>
          </Link>
          <UserMenu />
        </div>
      </header>
      <main id="main" className="shell__main">
        <Outlet />
      </main>
    </div>
  );
}