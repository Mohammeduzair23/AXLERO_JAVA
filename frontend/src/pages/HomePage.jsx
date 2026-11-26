import { useAuth } from '../context/AuthContext';
import BrandMark from '../components/BrandMark';

export default function HomePage() {
  const { user, logout } = useAuth();

  return (
    <main className="home">
      <header className="home__header">
        <a className="home__brand" href="/" aria-label="Axlero Dashboards home">
          <BrandMark />
          <span>Axlero Dashboards</span>
        </a>
        <button className="btn btn--secondary" type="button" onClick={logout}>
          Sign out
        </button>
      </header>
      <section className="home__content" aria-labelledby="home-title">
        <p className="home__eyebrow">YOU'RE SIGNED IN</p>
        <h1 id="home-title">Welcome{user?.fullName ? `, ${user.fullName}` : ''}.</h1>
        <p>Your account is ready. Workspace and dashboard tools will be available here.</p>
      </section>
    </main>
  );
}
